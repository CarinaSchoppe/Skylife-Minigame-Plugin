package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.events.kit.KitSelectorListener
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.countdown.LobbyCountdown
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Represents the lobby state of a game, where players gather before the match starts.
 * Manages the lobby countdown and player setup.
 *
 * @param game The context of the game this state belongs to.
 */
class LobbyState(private val game: Game) : GameState {

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
     * Handles a player joining the lobby. Gives them the kit selector item
     * and starts the countdown if the minimum player count is reached.
     *
     * @param player The player who joined.
     */
    override fun playerJoined(player: Player) {
        player.inventory.clear()
        val kitSelector = ItemStack(Material.CHEST)
        val meta = kitSelector.itemMeta
        meta.displayName(Messages.legacy(KitSelectorListener.KIT_SELECTOR_ITEM_NAME))
        kitSelector.itemMeta = meta
        player.inventory.setItem(4, kitSelector) // Place in the middle of the hotbar

        if (game.livingPlayers.size >= game.minPlayers && !countdown.isRunning) {
            countdown.start()
        }
        // Notify all players about the new player joining with current player count
        val joinMessage = Messages.PLAYER_JOINED(
            playerName = player.name,
            playerCount = game.livingPlayers.size,
            maxPlayers = game.maxPlayers
        )
        game.broadcast(joinMessage)
    }

    /**
     * Handles a player leaving the lobby. Removes their kit selection and stops
     * the countdown if the player count drops below the minimum.
     *
     * @param player The player who left.
     */
    override fun playerLeft(player: Player) {
        KitManager.removePlayer(player)
        if (game.livingPlayers.size < game.minPlayers && countdown.isRunning) {
            countdown.stop()
            game.livingPlayers.forEach { p ->
                p.sendMessage(Messages.COUNTDOWN_STOPPED)
            }
        }
        // Notify all players about the player leaving
        game.broadcast(Messages.PLAYER_LEFT(player.name))
    }
}