package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.countdown.IngameCountdown
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.skills.SkillEffectsManager
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.GameMode
import org.bukkit.entity.Player

/**
 * Represents the active gameplay state of a game.
 * Manages player spawning, kit distribution, and monitors win conditions.
 *
 * @param game The context of the game this state belongs to.
 */
class IngameState(private val game: Game) : GameState {

    private val countdown = IngameCountdown(game)

    /**
     * Starts the ingame state. Teleports all players to the game arena,
     * gives them their selected kits, activates their skills, and starts the ingame countdown.
     */
    override fun start() {
        game.livingPlayers.forEach { player ->
            player.teleport(game.ingameLocation)
            player.inventory.clear()

            // Activate skills
            SkillsManager.activateSkills(player)
            SkillEffectsManager.applySkillEffects(player)

            // Give kit items
            KitManager.giveKitItems(player)
        }
        countdown.start()
    }

    /**
     * Stops the ingame countdown.
     */
    override fun stop() {
        countdown.stop()
    }

    /**
     * Handles a player joining mid-game, setting them as a spectator.
     *
     * @param player The player who joined.
     */
    override fun playerJoined(player: Player) {
        // Players joining mid-game are set to spectator
        player.gameMode = GameMode.SPECTATOR
        player.teleport(game.ingameLocation)
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