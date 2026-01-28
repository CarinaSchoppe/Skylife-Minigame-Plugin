package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration

/**
 * A countdown for the initial protection phase at the start of a game.
 * Prevents PvP for a short duration.
 *
 * @param game The game instance this countdown belongs to.
 */
class ProtectionCountdown(private val game: Game) : Countdown() {

    private var seconds = ConfigurationLoader.config.timer.PROTECTION_TIMER

    /**
     * Starts the protection countdown if it's not already running.
     */
    override fun start() {
        if (isRunning) return
        isRunning = true
        seconds = ConfigurationLoader.config.timer.PROTECTION_TIMER

        task = object : BukkitRunnable() {
            override fun run() {
                if (seconds == 0) {
                    game.livingPlayers.forEach { player ->
                        player.sendMessage(Messages.PROTECTION_ENDED)
                        player.showTitle(
                            Title.title(
                                Component.text("PvP Enabled!", Messages.ERROR_COLOR),
                                Component.text("Fight!", Messages.MESSAGE_COLOR),
                                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
                            )
                        )
                        player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f)
                    }

                    // Disable protection in IngameState
                    val state = game.currentState
                    if (state is IngameState) {
                        state.disableProtection()
                    }

                    // Check if game should end (only 1 or 0 players left)
                    com.carinaschoppe.skylife.game.managers.GameManager.checkGameOver(game)

                    stop()
                    return
                }

                if (seconds <= 5) {
                    game.livingPlayers.forEach { player ->
                        player.sendMessage(Messages.PROTECTION_ENDING(seconds))
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f)
                        player.showTitle(
                            Title.title(
                                Component.text("Protection: $seconds", Messages.MESSAGE_COLOR),
                                Component.empty(),
                                Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
                            )
                        )
                    }
                } else if (seconds % 5 == 0) {
                    game.livingPlayers.forEach { it.sendMessage(Messages.PROTECTION_ENDING(seconds)) }
                }

                seconds--
            }
        }.runTaskTimer(Skylife.instance, 0, 20)
    }

    /**
     * Stops the protection countdown.
     */
    override fun stop() {
        if (!isRunning) return
        task?.cancel()
        task = null
        isRunning = false
    }
}