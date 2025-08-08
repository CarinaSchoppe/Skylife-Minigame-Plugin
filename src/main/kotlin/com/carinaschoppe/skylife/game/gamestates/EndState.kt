package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.countdown.EndingCountdown
import com.carinaschoppe.skylife.game.managers.GameManager
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.entity.Player

/**
 * Represents the final state of a game after a winner has been determined.
 * Manages the ending countdown before the game fully resets.
 *
 * @param game The context of the game this state belongs to.
 */
class EndState(private val game: Game) : GameState {

    private val countdown = EndingCountdown(game)

    /**
     * Starts the ending state and its countdown.
     */
    override fun start() {
        // Announce winner, etc.
        countdown.start()
        GameManager.endingMatchMessage(game)
        //add winning Stats to Player
        if (game.livingPlayers.size == 1)
            StatsUtility.addWinStatsToPlayer(game.livingPlayers.firstOrNull() ?: return)
    }

    /**
     * Stops the ending countdown and triggers the full game stop in the cluster.
     */
    override fun stop() {
        countdown.stop()
        GameCluster.stopGame(game)
    }

    /**
     * Handles players joining during the end screen. They are simply ignored.
     *
     * @param player The player who joined.
     */
    override fun playerJoined(player: Player) {
        // No new players are handled in the ending state.
    }

    /**
     * Handles players leaving during the end screen. They are simply removed.
     *
     * @param player The player who left.
     */
    override fun playerLeft(player: Player) {
        // Player is already out of the game logic at this point.
    }
}