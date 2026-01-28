package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable

/**
 * A countdown specifically for the game lobby.
 * Manages the timer that starts the game when enough players have joined.
 *
 * @property game The game instance this countdown belongs to.
 */
class LobbyCountdown(private val game: Game) : Countdown() {

    var seconds = 15

    private val runnable = object : BukkitRunnable() {
        override fun run() {
            if (!isRunning) {
                cancel()
                return
            }

            if (game.livingPlayers.size < game.minPlayers) {
                // Reset countdown if players leave
                stop()
                game.livingPlayers.forEach { it.sendMessage(Messages.COUNTDOWN_STOPPED) }
                return
            }

            if (seconds <= 0) {
                GameCluster.startGame(game)
                stop()
                return
            }

            if (seconds <= 5) {
                game.livingPlayers.forEach { player ->
                    player.sendMessage(Messages.COUNTDOWN(seconds))
                    player.level = seconds
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f)
                }
            } else if (seconds % 5 == 0) {
                game.livingPlayers.forEach { player ->
                    player.sendMessage(Messages.COUNTDOWN(seconds))
                    player.level = seconds
                }
            }
            seconds--
        }
    }

    /**
     * Sets the countdown's remaining seconds to a new value.
     * The time will only be changed if the new value is less than the current time,
     * to prevent extending the countdown.
     *
     * @param newSeconds The new number of seconds for the countdown.
     */
    fun reduceTo(newSeconds: Int) {
        if (isRunning && newSeconds < this.seconds) {
            this.seconds = newSeconds
        }
    }

    /**
     * Starts the lobby countdown if it's not already running.
     * The countdown will only proceed if the minimum number of players is met.
     */
    override fun start() {
        if (isRunning) return
        isRunning = true
        seconds = 15 // Reset to default value
        task = runnable.runTaskTimer(Skylife.instance, 0, 20)
    }

    /**
     * Stops the lobby countdown and resets its timer.
     */
    override fun stop() {
        super.stop()
        seconds = 15 // Reset timer
    }
}
