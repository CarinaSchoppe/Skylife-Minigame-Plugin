package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * Listener to handle block breaking by players.
 *
 * This listener prevents players from breaking blocks unless they are in a game
 * that is currently in the `IngameState`. It cancels the event for players in
 * the lobby, in the ending phase, or for spectators.
 */
class PlayerBreaksBlockListener : Listener {

    /**
     * Fired when a player breaks a block.
     *
     * @param event The [BlockBreakEvent] triggered by the action.
     */
    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val game = GameCluster.getGame(player)

        // If the player is in a game managed by the plugin, apply custom rules.
        // Players are only allowed to break blocks during the IngameState.
        // In all other states (Lobby, Ending) or as a spectator, it's forbidden.
        if (game != null && game.currentState !is IngameState) {
            event.isCancelled = true
            player.sendMessage(Messages.CANT_BREAK_BLOCK)
        }
    }
}