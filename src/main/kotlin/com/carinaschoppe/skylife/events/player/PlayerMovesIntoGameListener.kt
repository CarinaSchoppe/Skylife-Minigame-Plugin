package com.carinaschoppe.skylife.events.player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent

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
        // Cancel the default portal teleportation
        event.isCancelled = true

        // Execute command to join a random game
        event.player.performCommand("join random")
    }
}