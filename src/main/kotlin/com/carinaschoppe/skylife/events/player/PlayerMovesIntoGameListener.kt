package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent

/**
 * Listens for players entering portal blocks and handles their transition into games.
 *
 * This listener is responsible for intercepting portal events and redirecting players
 * to a random game when they enter a portal. It's typically used for creating
 * "quick join" functionality where players can enter a portal to be placed in a game.
 *
 * The listener cancels the default portal teleportation and instead executes a
 * command to join a random game.
 */
class PlayerMovesIntoGameListener : Listener {

    /**
     * Handles the event when a player enters a portal.
     *
     * This method is triggered when a player enters any type of portal (Nether, End, etc.).
     * It cancels the default portal behavior and instead executes a command to join
     * a random game.
     *
     * @param event The PlayerPortalEvent that was triggered
     */
    @EventHandler(ignoreCancelled = true)
    fun onPlayerPortal(event: PlayerPortalEvent) {
        if (event.cause != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return
        }

        // Cancel the default end portal teleportation
        event.isCancelled = true

        // If the player is not already in a game, add them to a random lobby game.
        if (GameCluster.getGame(event.player) == null && !GameCluster.addPlayerToRandomGame(event.player)) {
            event.player.sendMessage(Messages.ERROR_NO_GAME)
        }
    }
}
