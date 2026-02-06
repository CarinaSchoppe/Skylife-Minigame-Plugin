package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.countdown.EndingCountdown
import com.carinaschoppe.skylife.game.managers.GameManager
import com.carinaschoppe.skylife.platform.PluginContext
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import com.carinaschoppe.skylife.utility.ui.ExitDoorItem
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
class EndState(private val game: Game) : GameState<Player> {

    private val countdown = EndingCountdown(game)

    /**
     * Starts the ending state and its countdown.
     * Teleports all players to lobby and sets them to adventure mode.
     */
    override fun start() {
        // Teleport all players (living + spectators) to game lobby in adventure mode
        val lobbyLocation = com.carinaschoppe.skylife.game.managers.MapManager.locationWorldConverter(
            game.lobbyLocation,
            game
        )

        game.getAllPlayers().forEach { player ->
            player.gameMode = org.bukkit.GameMode.ADVENTURE
            player.teleport(lobbyLocation)

            // Clear inventory and give exit door
            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)
            player.inventory.setItem(8, ExitDoorItem.create())

            // Make all players visible to each other (remove spectator invisibility)
            game.getAllPlayers().forEach { other ->
                player.showPlayer(PluginContext.plugin, other)
                other.showPlayer(PluginContext.plugin, player)
            }
        }

        countdown.start()
        GameManager.endingMatchMessage(game)

        // Award coins for playing to all players
        game.getAllPlayers().forEach { player ->
            val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(player)
            val coinsEarned = com.carinaschoppe.skylife.economy.CoinManager.awardGameCoins(player.uniqueId, rank)
            player.sendMessage(Messages.COINS_EARNED_GAME(coinsEarned))
        }

        // Handle winner
        if (game.livingPlayers.size == 1) {
            val winner = game.livingPlayers.firstOrNull() ?: return
            StatsUtility.addWinStatsToPlayer(winner)

            // Award win coins
            val winnerRank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(winner)
            val winCoins = com.carinaschoppe.skylife.economy.CoinManager.awardWinCoins(winner.uniqueId, winnerRank)
            winner.sendMessage(Messages.COINS_EARNED_WIN(winCoins))

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
        }.runTaskTimer(PluginContext.plugin, 0L, 20L)
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

        // Schedule world cleanup with a small delay to ensure all players have been teleported
        // This prevents potential issues with players still being in the world during unload
        org.bukkit.Bukkit.getScheduler().runTaskLater(PluginContext.plugin, Runnable {
            com.carinaschoppe.skylife.game.managers.MapManager.unloadAndDeleteWorld(game.gameID)
        }, 20L) // 1 second delay
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
