package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import org.bukkit.scheduler.BukkitRunnable

/**
 * A countdown for the game's ending phase.
 * Manages the timer before the game fully resets and sends players back.
 *
 * @param game The game instance this countdown belongs to.
 */
class EndingCountdown(private val game: Game) : Countdown() {

    private var seconds = 10

    /**
     * Starts the ending countdown if it's not already running.
     */
    override fun start() {
        if (isRunning) return
        isRunning = true

        task = object : BukkitRunnable() {
            override fun run() {
                if (seconds <= 0) {
                    GameCluster.stopGame(game) // Fully stop and reset the game
                    stop()
                    return
                }
                seconds--
            }
        }.runTaskTimer(Skylife.instance, 0, 20)
    }

    /**
     * Stops the ending countdown.
     */
    override fun stop() {
        if (!isRunning) return
        task?.cancel()
        task = null
        isRunning = false
    }
}