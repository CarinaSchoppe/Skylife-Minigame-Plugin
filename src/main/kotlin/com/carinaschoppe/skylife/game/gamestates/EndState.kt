package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.countdown.EndingCountdown
import com.carinaschoppe.skylife.game.managers.GameManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration

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
        countdown.start()
        GameManager.endingMatchMessage(game)

        // Handle winner
        if (game.livingPlayers.size == 1) {
            val winner = game.livingPlayers.firstOrNull() ?: return
            StatsUtility.addWinStatsToPlayer(winner)

            // Launch fireworks above winner's head
            launchFireworks(winner)

            // Show victory title to winner
            winner.showTitle(
                Title.title(
                    Component.text("VICTORY!", Messages.SUCCESS_COLOR),
                    Component.text("You are the champion!", Messages.MESSAGE_COLOR),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))
                )
            )

            // Show defeat title to spectators
            game.spectators.forEach { spectator ->
                spectator.showTitle(
                    Title.title(
                        Component.text("Game Over!", Messages.ERROR_COLOR),
                        Component.text("${winner.name} won the game!", Messages.MESSAGE_COLOR),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))
                    )
                )
            }
        }
    }

    private fun launchFireworks(winner: Player) {
        var count = 0
        object : BukkitRunnable() {
            override fun run() {
                if (count >= 5) {
                    cancel()
                    return
                }

                val firework = winner.world.spawnEntity(
                    winner.location.add(0.0, 1.0, 0.0),
                    EntityType.FIREWORK_ROCKET
                ) as Firework

                val meta = firework.fireworkMeta
                meta.power = 1
                meta.addEffect(
                    FireworkEffect.builder()
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .withColor(Color.YELLOW, Color.ORANGE, Color.RED)
                        .withFade(Color.WHITE)
                        .trail(true)
                        .flicker(true)
                        .build()
                )
                firework.fireworkMeta = meta

                count++
            }
        }.runTaskTimer(Skylife.instance, 0L, 20L)
    }

    /**
     * Stops the ending countdown and triggers the full game stop in the cluster.
     * This includes cleaning up the game world.
     */
    override fun stop() {
        countdown.stop()

        // GameCluster.stopGame() will handle:
        // - Player cleanup (scoreboard, skills, inventory)
        // - Teleportation to hub
        // - Creating new game instance
        GameCluster.stopGame(game)

        // After GameCluster.stopGame() has teleported all players out,
        // we can safely unload and delete the game world
        com.carinaschoppe.skylife.game.managers.MapManager.unloadAndDeleteWorld(game.gameID)
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