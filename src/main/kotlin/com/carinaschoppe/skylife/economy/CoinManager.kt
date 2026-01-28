package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages player coins and transactions.
 */
object CoinManager {

    // Cache: Player UUID -> Coins
    private val coinCache = ConcurrentHashMap<UUID, Int>()

    // Coin rewards
    const val COINS_PER_GAME = 10
    const val COINS_PER_KILL = 25
    const val COINS_PER_WIN = 100

    /**
     * Loads coins from database into cache.
     */
    fun loadCoins() {
        transaction {
            PlayerEconomy.all().forEach { economy ->
                coinCache[UUID.fromString(economy.playerUUID)] = economy.coins
            }
        }
    }

    /**
     * Gets a player's coin balance.
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
     * @param player The player UUID
     * @param amount The amount to add (can be negative)
     * @param multiplier Rank multiplier (1.0 for User, 2.0 for VIP, 4.0 for VIP+)
     */
    fun addCoins(player: UUID, amount: Int, multiplier: Double = 1.0) {
        val finalAmount = (amount * multiplier).toInt()
        val currentCoins = getCoins(player)
        val newBalance = currentCoins + finalAmount

        coinCache[player] = newBalance

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
    }

    /**
     * Removes coins from a player's balance.
     * @return true if successful, false if insufficient funds
     */
    fun removeCoins(player: UUID, amount: Int): Boolean {
        val currentCoins = getCoins(player)
        if (currentCoins < amount) {
            return false
        }

        addCoins(player, -amount, 1.0) // No multiplier for removal
        return true
    }

    /**
     * Checks if a player can afford something.
     */
    fun canAfford(player: UUID, amount: Int): Boolean {
        return getCoins(player) >= amount
    }

    /**
     * Awards coins for playing a game.
     */
    fun awardGameCoins(player: UUID, rank: PlayerRank) {
        addCoins(player, COINS_PER_GAME, rank.coinMultiplier)
    }

    /**
     * Awards coins for a kill.
     */
    fun awardKillCoins(player: UUID, rank: PlayerRank) {
        addCoins(player, COINS_PER_KILL, rank.coinMultiplier)
    }

    /**
     * Awards coins for winning.
     */
    fun awardWinCoins(player: UUID, rank: PlayerRank) {
        addCoins(player, COINS_PER_WIN, rank.coinMultiplier)
    }
}
