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
     * @return true if successful, false if insufficient funds or already unlocked
     */
    fun purchaseKit(player: UUID, kit: Kit): Result<Unit> {
        // Check if already unlocked
        if (hasUnlocked(player, kit)) {
            return Result.failure(Exception("You already own this kit!"))
        }

        // Check if can afford
        if (!CoinManager.canAfford(player, kit.rarity.price)) {
            return Result.failure(Exception("Insufficient coins! Need ${kit.rarity.price}, have ${CoinManager.getCoins(player)}"))
        }

        // Remove coins
        val success = CoinManager.removeCoins(player, kit.rarity.price)
        if (!success) {
            return Result.failure(Exception("Failed to process payment"))
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
