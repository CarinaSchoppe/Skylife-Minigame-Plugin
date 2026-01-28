package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages player coins and transactions.
 * Provides thread-safe operations with database-first persistence.
 *
 * Base coin rewards:
 * - Playing a game: 10 coins
 * - Getting a kill: 25 coins
 * - Winning a game: 100 coins
 *
 * These base amounts are multiplied by player rank multipliers (1x/2x/4x).
 */
object CoinManager {

    // Cache: Player UUID -> Coins
    private val coinCache = ConcurrentHashMap<UUID, Int>()

    // Coin rewards
    const val COINS_PER_GAME = 10
    const val COINS_PER_KILL = 25
    const val COINS_PER_WIN = 100

    /**
     * Loads all player coin balances from the database into memory cache.
     * Should be called during plugin initialization.
     */
    fun loadCoins() {
        transaction {
            PlayerEconomy.all().forEach { economy ->
                coinCache[UUID.fromString(economy.playerUUID)] = economy.coins
            }
        }
    }

    /**
     * Gets a player's current coin balance.
     * Returns from cache if available, otherwise queries database.
     *
     * @param player The player's UUID
     * @return The player's coin balance (defaults to 0 if player not found)
     */
    fun getCoins(player: UUID): Int {
        return coinCache.getOrPut(player) {
            transaction {
                PlayerEconomy.find { PlayerEconomyTable.playerUUID eq player.toString() }
                    .firstOrNull()?.coins ?: 0
            }
        }
    }

    /**
     * Adds coins to a player's balance.
     * Uses database-first approach: writes to database before updating cache for crash safety.
     * Balance cannot go below 0.
     *
     * @param player The player's UUID
     * @param amount The base amount to add (can be negative for deductions)
     * @param multiplier Rank multiplier to apply (1.0 for User, 2.0 for VIP, 4.0 for VIP+)
     */
    fun addCoins(player: UUID, amount: Int, multiplier: Double = 1.0) {
        val finalAmount = (amount * multiplier).toInt()
        val currentCoins = getCoins(player)
        val newBalance = maxOf(0, currentCoins + finalAmount) // Cannot go below 0

        // Write to database first for safety (in case of crash)
        transaction {
            val economy = PlayerEconomy.find { PlayerEconomyTable.playerUUID eq player.toString() }.firstOrNull()
            if (economy != null) {
                economy.coins = newBalance
            } else {
                PlayerEconomy.new {
                    this.playerUUID = player.toString()
                    this.coins = newBalance
                }
            }
        }

        // Update cache after successful DB write
        coinCache[player] = newBalance
    }

    /**
     * Removes coins from a player's balance.
     * If player has insufficient coins, removes all remaining coins instead.
     *
     * @param player The player's UUID
     * @param amount The amount of coins to remove
     * @return true if player had enough coins (full amount removed), false if insufficient (all coins removed)
     */
    fun removeCoins(player: UUID, amount: Int): Boolean {
        val currentCoins = getCoins(player)
        if (currentCoins < amount) {
            // Remove all coins if not enough
            if (currentCoins > 0) {
                addCoins(player, -currentCoins, 1.0)
            }
            return false
        }

        addCoins(player, -amount, 1.0) // No multiplier for removal
        return true
    }

    /**
     * Checks if a player has enough coins to afford a purchase.
     *
     * @param player The player's UUID
     * @param amount The price to check
     * @return true if player has at least the specified amount, false otherwise
     */
    fun canAfford(player: UUID, amount: Int): Boolean {
        return getCoins(player) >= amount
    }

    /**
     * Awards coins for playing a game.
     * Base reward: 10 coins, multiplied by player's rank multiplier.
     *
     * @param player The player's UUID
     * @param rank The player's rank (determines multiplier)
     * @return The actual amount of coins earned (with rank multiplier applied)
     */
    fun awardGameCoins(player: UUID, rank: PlayerRank): Int {
        val amount = (COINS_PER_GAME * rank.coinMultiplier).toInt()
        addCoins(player, COINS_PER_GAME, rank.coinMultiplier)
        return amount
    }

    /**
     * Awards coins for getting a kill in game.
     * Base reward: 25 coins, multiplied by player's rank multiplier.
     *
     * @param player The player's UUID
     * @param rank The player's rank (determines multiplier)
     * @return The actual amount of coins earned (with rank multiplier applied)
     */
    fun awardKillCoins(player: UUID, rank: PlayerRank): Int {
        val amount = (COINS_PER_KILL * rank.coinMultiplier).toInt()
        addCoins(player, COINS_PER_KILL, rank.coinMultiplier)
        return amount
    }

    /**
     * Awards coins for winning a game.
     * Base reward: 100 coins, multiplied by player's rank multiplier.
     *
     * @param player The player's UUID
     * @param rank The player's rank (determines multiplier)
     * @return The actual amount of coins earned (with rank multiplier applied)
     */
    fun awardWinCoins(player: UUID, rank: PlayerRank): Int {
        val amount = (COINS_PER_WIN * rank.coinMultiplier).toInt()
        addCoins(player, COINS_PER_WIN, rank.coinMultiplier)
        return amount
    }
}
