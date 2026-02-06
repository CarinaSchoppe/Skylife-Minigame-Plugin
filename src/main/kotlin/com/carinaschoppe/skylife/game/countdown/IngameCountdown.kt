package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.platform.PluginContext
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.scheduler.BukkitRunnable

/**
 * A countdown for the main gameplay phase.
 * Manages the overall game timer.
 *
 * @property game The game instance this countdown belongs to.
 */
class IngameCountdown(private val game: Game) : Countdown() {

    private var seconds = 600 // 10 minutes

    /**
     * Starts the ingame countdown if it's not already running.
     */
    override fun start() {
        if (isRunning) return
        isRunning = true
        seconds = 600 // Reset to 10 minutes

        // Create a new BukkitRunnable each time to avoid "Already scheduled" error
        task = object : BukkitRunnable() {
            override fun run() {
                if (!isRunning) {
                    cancel()
                    return
                }

                if (seconds <= 0) {
                    GameCluster.stopGame(game) // End game when time is up
                    stop()
                    return
                }

                // Announce time remaining at one-minute intervals
                if (seconds % 60 == 0) {
                    val minutes = seconds / 60
                    val message = Messages.parse("<gray>Only <yellow>$minutes minute${if (minutes != 1) "s" else ""}</yellow><gray> remaining!</gray>")
                    game.livingPlayers.forEach { it.sendMessage(message) }
                }

                seconds--
            }
        }.runTaskTimer(PluginContext.plugin, 0, 20)
    }
}
