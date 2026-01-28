package com.carinaschoppe.skylife.utility.scoreboard

import com.carinaschoppe.skylife.game.GameCluster
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

/**
 * Task that periodically updates lobby scoreboards for all players in the lobby.
 */
object LobbyScoreboardUpdateTask {

    private var task: BukkitTask? = null

    /**
     * Starts the lobby scoreboard update task.
     * Updates every second.
     * @param plugin The plugin instance
     */
    fun start(plugin: Plugin) {
        if (task != null) {
            return
        }

        // Update every 20 ticks (1 second)
        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            updateAllLobbyScoreboards()
        }, 20L, 20L)
    }

    /**
     * Stops the lobby scoreboard update task.
     */
    fun stop() {
        task?.cancel()
        task = null
    }

    /**
     * Updates all lobby scoreboards for players not in a game.
     */
    private fun updateAllLobbyScoreboards() {
        Bukkit.getOnlinePlayers().forEach { player ->
            // Only update if player is not in a game
            if (GameCluster.getGame(player) == null) {
                val scoreboard = player.scoreboard
                val objective = scoreboard.getObjective("skylife_lobby")
                if (objective != null) {
                    LobbyScoreboardManager.updateLobbyScoreboard(player)
                }
            }
        }
    }
}
