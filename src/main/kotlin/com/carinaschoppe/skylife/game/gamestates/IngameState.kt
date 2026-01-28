package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.countdown.IngameCountdown
import com.carinaschoppe.skylife.game.countdown.ProtectionCountdown
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.game.managers.MapManager
import com.carinaschoppe.skylife.skills.SkillEffectsManager
import com.carinaschoppe.skylife.skills.SkillsManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.time.Duration

/**
 * Represents the active gameplay state of a game.
 * Manages player spawning, skill activation, and monitors win conditions.
 *
 * @param game The context of the game this state belongs to.
 */
class IngameState(private val game: Game) : GameState {

    private val countdown = IngameCountdown(game)
    private val protectionCountdown = ProtectionCountdown(game)

    var protectionActive = true
        private set

    /**
     * Starts the ingame state. Teleports all players to spawn locations,
     * gives them their skill items, and starts the ingame countdown.
     */
    override fun start() {
        // Set protection active
        protectionActive = true

        // Get spawn locations
        val spawnLocations = game.pattern.gameLocationManager.spawnLocations.toList()

        game.livingPlayers.forEachIndexed { index, player ->
            // Teleport to spawn location (cycle through available spawns)
            val spawnIndex = index % spawnLocations.size
            val skylifeSpawnLoc = GameLocationManager.skylifeLocationToLocationConverter(spawnLocations[spawnIndex])

            if (skylifeSpawnLoc == null) {
                org.bukkit.Bukkit.getLogger().severe("[IngameState] Failed to convert spawn location for player ${player.name}")
                player.sendMessage(Messages.PREFIX.append(Component.text("Failed to load spawn location", net.kyori.adventure.text.format.NamedTextColor.RED)))
                return
            }

            val spawnLocation = MapManager.locationWorldConverter(skylifeSpawnLoc, game)
            player.teleport(spawnLocation)

            // Clear inventory and armor
            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)

            // Set survival mode
            player.gameMode = GameMode.SURVIVAL


            // Activate skills and give their items
            SkillsManager.activateSkills(player)
            SkillEffectsManager.applySkillEffects(player)

            // Reset GOD skill counter for this player
            com.carinaschoppe.skylife.skills.SkillPassiveItemsTask.resetGodCounter(player.uniqueId)

            // Show game start title
            player.showTitle(
                Title.title(
                    Component.text("Game Started!", Messages.MESSAGE_COLOR),
                    Component.text("Survive and be the last one standing!", Messages.SUCCESS_COLOR),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
                )
            )
        }

        // Disable weather in game world
        game.world.setStorm(false)
        game.world.isThundering = false
        game.world.weatherDuration = 0

        // Start protection countdown
        protectionCountdown.start()

        countdown.start()
    }

    /**
     * Disables protection phase - called by ProtectionCountdown when it ends.
     */
    fun disableProtection() {
        protectionActive = false
    }

    /**
     * Stops the ingame countdown.
     */
    override fun stop() {
        countdown.stop()
        protectionCountdown.stop()
    }

    /**
     * Handles a player joining mid-game, setting them as a spectator.
     *
     * @param player The player who joined.
     */
    override fun playerJoined(player: Player) {
        // Players joining mid-game are set to spectator
        player.gameMode = GameMode.SPECTATOR

        // Teleport to ingame location in the game's dedicated world
        val ingameInGameWorld = MapManager.locationWorldConverter(game.ingameLocation, game)
        player.teleport(ingameInGameWorld)
    }

    /**
     * Handles a player leaving the game. Checks if the game should end
     * due to a lack of players.
     *
     * @param player The player who left.
     */
    override fun playerLeft(player: Player) {
        if (game.livingPlayers.size <= 1) {
            GameCluster.stopGame(game)
        }
    }
}