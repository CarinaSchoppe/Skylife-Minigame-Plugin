package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

/**
 * Listener that allows players to join a random game by stepping into a portal.
 *
 * This listener specifically checks if a player moves onto an `END_PORTAL` block.
 * If the player is not already in a game (i.e., they are in the hub), it will
 * automatically add them to a random available game, serving as a "quick join" portal.
 */
class PlayerEntersPortalListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        // Optimization: Only proceed if the player has moved to a new block.
        if (!event.hasChangedBlock()) {
            return
        }

        // Check if the destination block is the trigger for joining a game.
        if (event.to.block.type == Material.END_PORTAL) {
            val player = event.player

            // If the player is not already in a game, add them to a random one.
            // This prevents players who are already in a lobby or match from being moved.
            if (GameCluster.getGame(player) == null) {
                GameCluster.addPlayerToRandomGame(player)
            }
        }
    }
}