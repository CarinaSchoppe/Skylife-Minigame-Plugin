package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

/**
 * Listener to handle block placement by players.
 *
 * This listener prevents players from placing blocks unless they are in a game
 * that is currently in the `IngameState`. It cancels the event for players in
 * the lobby, in the ending phase, or for spectators.
 */
class PlayerPlacesBlockListener : Listener {

    /**
     * Fired when a player places a block.
     *
     * @param event The [BlockPlaceEvent] triggered by the action.
     */
    @EventHandler(ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val game = GameCluster.getGame(player) ?: return


        // Players are only allowed to place blocks during the IngameState.
        // In all other states (Lobby, Ending) or as a spectator, it's forbidden.
        if (game.currentState !is IngameState) {
            event.isCancelled = true
            player.sendMessage(Messages.CANT_PLACE_BLOCK)
        }
    }
}