package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.countdown.LobbyCountdown
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.ExitDoorItem
import com.carinaschoppe.skylife.utility.ui.SkillsGui
import org.bukkit.entity.Player

/**
 * Represents the lobby state of a game, where players gather before the match starts.
 * Manages the lobby countdown and player setup.
 *
 * @param game The context of the game this state belongs to.
 */
class LobbyState(private val game: Game) : GameState<Player> {

    /**
     * The countdown timer for the lobby phase.
     * This is exposed to allow commands like quickstart to interact with it.
     */
    val countdown = LobbyCountdown(game)

    /**
     * Starts the lobby state logic, primarily the countdown if conditions are met.
     */
    override fun start() {
        // The countdown is automatically managed by playerJoined and playerLeft events.
    }

    /**
     * Stops the lobby countdown.
     */
    override fun stop() {
        countdown.stop()
    }

    /**
     * Handles a player joining the lobby. Gives them the skills selector item
     * and starts the countdown if the minimum player count is reached.
     *
     * @param player The player who joined.
     */
    override fun playerJoined(player: Player) {
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4)

        // Set adventure mode in lobby
        player.gameMode = org.bukkit.GameMode.ADVENTURE

        // Skills selector in middle
        player.inventory.setItem(4, SkillsGui.createSkillsMenuItem())

        // Exit door in last slot
        player.inventory.setItem(8, ExitDoorItem.create())

        val minPlayersToStart = game.pattern.minPlayersToStart
        if (game.livingPlayers.size >= minPlayersToStart && !countdown.isRunning) {
            countdown.start()
        }
        // Notify all players about the new player joining with current player count
        val joinMessage = Messages.PLAYER_JOINED(
            player.name,
            game.livingPlayers.size,
            game.maxPlayers
        )
        game.broadcast(joinMessage)
    }

    /**
     * Handles a player leaving the lobby and stops
     * the countdown if the player count drops below the minimum.
     *
     * @param player The player who left.
     */
    override fun playerLeft(player: Player) {
        val minPlayersToStart = game.pattern.minPlayersToStart
        if (game.livingPlayers.size < minPlayersToStart && countdown.isRunning) {
            countdown.stop()
            game.livingPlayers.forEach { p ->
                p.sendMessage(Messages.COUNTDOWN_STOPPED)
            }
        }
        // Notify all players about the player leaving
        game.broadcast(Messages.PLAYER_LEFT_GAME_BROADCAST(player.name))
    }
}
