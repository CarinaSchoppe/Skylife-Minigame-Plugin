package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.game.managers.MapManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.scoreboard.ScoreboardManager
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

/**
 * Listener to handle all logic related to a player's death within a game.
 *
 * This is a critical listener that manages:
 * - Suppressing the default death message.
 * - Transitioning the player from a living player to a spectator.
 * - Respawning the player at the spectator location.
 * - Updating statistics for deaths and kills.
 * - Broadcasting custom death/kill messages to all participants in the game.
 * - Updating scoreboards.
 * - Checking if the game has a winner after the death.
 */
class PlayerDeathListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        // Suppress the default Minecraft death message
        event.deathMessage(null)

        val player = event.player
        val game = GameCluster.getGame(player) ?: return // Exit if the player is not in a managed game

        // --- Player State Transition ---
        game.livingPlayers.remove(player)
        game.spectators.add(player)

        // --- Respawn Logic ---
        // Set respawn location and handle respawn for Paper 1.21
        val spectatorLocation = MapManager.locationWorldConverter(
            GameLocationManager.skylifeLocationToLocationConverter(game.pattern.gameLocationManager.spectatorLocation),
            game
        )

        // Set the respawn location for the player
        player.setRespawnLocation(spectatorLocation, true)

        // Schedule the respawn and spectator mode for the next tick
        Bukkit.getScheduler().runTaskLater(Skylife.instance, Runnable {
            // Set spectator mode
            player.gameMode = GameMode.SPECTATOR
            // Teleport to spectator location (in case the respawn location didn't work)
            player.teleport(spectatorLocation)
        }, 1L)

        // --- Statistics and Messaging ---
        StatsUtility.addDeathStatsToPlayer(player)

        val killer = player.killer // This is a nullable Player, cleaner than casting

        if (killer != null && game.livingPlayers.contains(killer)) {
            // Case: Player was killed by another player in the same game
            StatsUtility.addKillStatsToPlayer(killer)
            game.gameKills[killer.uniqueId] = game.gameKills.getOrDefault(killer.uniqueId, 0) + 1
            game.broadcast(Messages.PLAYER_KILLED(player.name, killer.name))
        } else {
            // Case: Player died from environment or other causes
            game.broadcast(Messages.PLAYER_DIED(player.name))
        }

        game.broadcast(Messages.PLAYERS_REMAINING(game.livingPlayers.size))

        // --- Scoreboard and Game State Update ---
        // Update scoreboards for everyone in the game
        game.getAllPlayers().forEach { p -> ScoreboardManager.updateScoreboard(p, game) }

        // Check if the game is over
        if (game.livingPlayers.size <= 1) {
            game.stop()
        }
    }
}