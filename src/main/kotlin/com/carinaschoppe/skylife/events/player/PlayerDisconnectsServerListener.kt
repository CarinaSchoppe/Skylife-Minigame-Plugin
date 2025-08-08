package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.scoreboard.ScoreboardManager
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listener to handle players disconnecting from the server.
 *
 * If a player is in a game when they disconnect, this listener ensures they are
 * properly removed from the game, a notification is sent to the other players,
 * and the game state is updated accordingly.
 */
class PlayerDisconnectsServerListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val game = GameCluster.getGame(player)

        // Suppress the default quit message to replace it with a custom one.
        event.quitMessage(null)

        // Broadcast player left message to all players
        player.server.broadcast(Messages.PLAYER_LEFT_GAME_BROADCAST(player.name))

        // Only handle game-related cleanup if the player was in a managed game
        if (game != null) {
            // Remove player from the game
            game.livingPlayers.remove(player)
            game.spectators.remove(player)

            // Update statistics
            StatsUtility.addStatsToPlayerWhenLeave(player)

            // Notify the remaining players in the game.
            if (game.livingPlayers.isNotEmpty() || game.spectators.isNotEmpty()) {
                game.broadcast(Messages.PLAYER_LEFT_GAME_BROADCAST(player.name))
            }

            // Check if the game is over after the player left.
            if (game.livingPlayers.size <= 1) {
                if (game.livingPlayers.size == 1) {
                    // If there's one player left, they win
                    val winner = game.livingPlayers[0]
                    game.broadcast(Messages.PLAYER_WON(winner.name))
                }
                game.stop()
            }

            // Update scoreboards for remaining players
            game.getAllPlayers().forEach { p ->
                ScoreboardManager.updateScoreboard(p, game)
            }
        }
        // If the player is not in a game, do nothing and let the default quit message appear.
    }
}