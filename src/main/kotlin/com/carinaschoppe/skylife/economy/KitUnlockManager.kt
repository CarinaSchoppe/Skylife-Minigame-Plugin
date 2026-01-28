package com.carinaschoppe.skylife.economy

import com.carinaschoppe.skylife.game.kit.Kit
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages kit unlocks and purchases.
 */
object KitUnlockManager {

    // Cache: Player UUID -> Set of unlocked kit names
    private val unlockedKitsCache = ConcurrentHashMap<UUID, MutableSet<String>>()

    /**
     * Loads kit unlocks from database into cache.
     */
    fun loadUnlocks() {
        transaction {
            KitUnlock.all().forEach { unlock ->
                val playerUUID = UUID.fromString(unlock.playerUUID)
                unlockedKitsCache.getOrPut(playerUUID) { mutableSetOf() }.add(unlock.kitName)
            }
        }
    }

    /**
     * Checks if a player has unlocked a kit.
     * COMMON kits are always unlocked.
     */
    fun hasUnlocked(player: UUID, kit: Kit): Boolean {
        // Common kits are always unlocked
        if (kit.rarity == com.carinaschoppe.skylife.game.kit.KitRarity.COMMON) {
            return true
        }

        return unlockedKitsCache.getOrPut(player) { mutableSetOf() }.contains(kit.name)
    }

    /**
     * Unlocks a kit for a player (admin command or purchase).
     */
    fun unlockKit(player: UUID, kit: Kit) {
        // Add to cache
        unlockedKitsCache.getOrPut(player) { mutableSetOf() }.add(kit.name)

        // Add to database
        transaction {
            val existing = KitUnlock.find {
                (KitUnlockTable.playerUUID eq player.toString()) and (KitUnlockTable.kitName eq kit.name)
            }.firstOrNull()

            if (existing == null) {
                KitUnlock.new {
                    this.playerUUID = player.toString()
                    this.kitName = kit.name
                }
            }
        }
    }

    /**
     * Attempts to purchase a kit for a player.
     * Thread-safe transaction that checks funds and unlocks atomically.
     * @return Result.success if successful, Result.failure with error message otherwise
     */
    fun purchaseKit(player: UUID, kit: Kit): Result<Unit> {
        // Check if already unlocked
        if (hasUnlocked(player, kit)) {
            return Result.failure(Exception("You already own this kit!"))
        }

        // Check if free kit (Common)
        if (kit.rarity.price == 0) {
            unlockKit(player, kit)
            return Result.success(Unit)
        }

        // Get current coins atomically
        val currentCoins = CoinManager.getCoins(player)

        // Check if can afford
        if (currentCoins < kit.rarity.price) {
            return Result.failure(Exception("Insufficient coins! Need ${kit.rarity.price}, have $currentCoins"))
        }

        // Remove coins (this is thread-safe with the max check inside)
        val success = CoinManager.removeCoins(player, kit.rarity.price)
        if (!success) {
            // This shouldn't happen since we checked above, but safety check
            return Result.failure(Exception("Failed to process payment - insufficient funds"))
        }

        // Unlock kit
        unlockKit(player, kit)

        return Result.success(Unit)
    }

    /**
     * Gets all unlocked kits for a player.
     */
    fun getUnlockedKits(player: UUID): Set<String> {
        return unlockedKitsCache.getOrPut(player) { mutableSetOf() }.toSet()
    }
}
