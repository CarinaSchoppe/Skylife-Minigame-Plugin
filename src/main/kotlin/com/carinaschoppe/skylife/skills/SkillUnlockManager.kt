package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.economy.CoinManager
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages skill unlocks and purchases.
 * Provides thread-safe operations for checking, unlocking, and purchasing skills.
 */
object SkillUnlockManager {

    // Cache: Player UUID -> Set of unlocked skill names
    private val unlockedSkillsCache = ConcurrentHashMap<UUID, MutableSet<String>>()

    /**
     * Loads all skill unlocks from the database into memory cache.
     * Should be called during plugin initialization.
     */
    fun loadUnlocks() {
        transaction {
            SkillUnlock.all().forEach { unlock ->
                val playerUUID = UUID.fromString(unlock.playerUUID)
                unlockedSkillsCache.getOrPut(playerUUID) { mutableSetOf() }.add(unlock.skillName)
            }
        }
    }

    /**
     * Checks if a player has unlocked a specific skill.
     * COMMON rarity skills are always considered unlocked.
     *
     * @param player The player's UUID
     * @param skill The skill to check
     * @return true if the skill is unlocked or is COMMON rarity, false otherwise
     */
    fun hasUnlocked(player: UUID, skill: Skill): Boolean {
        // Common skills are always unlocked
        if (skill.rarity.price == 0) {
            return true
        }

        return unlockedSkillsCache.getOrPut(player) { mutableSetOf() }.contains(skill.name)
    }

    /**
     * Unlocks a skill for a player.
     * Updates both cache and database. Used after successful purchases or admin grants.
     *
     * @param player The player's UUID
     * @param skill The skill to unlock
     */
    fun unlockSkill(player: UUID, skill: Skill) {
        // Add to cache
        unlockedSkillsCache.getOrPut(player) { mutableSetOf() }.add(skill.name)

        // Add to database
        transaction {
            val existing = SkillUnlock.find {
                (SkillUnlockTable.playerUUID eq player.toString()) and (SkillUnlockTable.skillName eq skill.name)
            }.firstOrNull()

            if (existing == null) {
                SkillUnlock.new {
                    this.playerUUID = player.toString()
                    this.skillName = skill.name
                }
            }
        }
    }

    /**
     * Attempts to purchase a skill for a player.
     * Performs thread-safe transaction that checks funds, deducts coins, and unlocks the skill.
     * COMMON rarity skills (price 0) are unlocked for free.
     *
     * @param player The player's UUID
     * @param skill The skill to purchase
     * @return Result.success if purchase succeeded, Result.failure with error message if failed
     */
    fun purchaseSkill(player: UUID, skill: Skill): Result<Unit> {
        // Check if already unlocked
        if (hasUnlocked(player, skill)) {
            return Result.failure(Exception("You already own this skill!"))
        }

        // Check if free skill (Common)
        if (skill.rarity.price == 0) {
            unlockSkill(player, skill)
            return Result.success(Unit)
        }

        // Get current coins atomically
        val currentCoins = CoinManager.getCoins(player)

        // Check if can afford
        if (currentCoins < skill.rarity.price) {
            return Result.failure(Exception("Insufficient coins! Need ${skill.rarity.price}, have $currentCoins"))
        }

        // Remove coins (this is thread-safe with the max check inside)
        val success = CoinManager.removeCoins(player, skill.rarity.price)
        if (!success) {
            return Result.failure(Exception("Failed to process payment - insufficient funds"))
        }

        // Unlock skill
        unlockSkill(player, skill)

        return Result.success(Unit)
    }

    /**
     * Gets all unlocked skill names for a player.
     *
     * @param player The player's UUID
     * @return Immutable set of unlocked skill names (does not include COMMON skills automatically)
     */
    fun getUnlockedSkills(player: UUID): Set<String> {
        return unlockedSkillsCache.getOrPut(player) { mutableSetOf() }.toSet()
    }
}
