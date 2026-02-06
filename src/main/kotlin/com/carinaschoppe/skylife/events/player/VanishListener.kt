package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.miscellaneous.VanishManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Event listener for vanish-related events.
 * Handles automatic vanish state management on player join, quit, and death.
 *
 * Security features:
 * - Auto-hides vanished players from joining players
 * - Auto-unvanishes players on quit to prevent state persistence
 * - Auto-unvanishes players on death to ensure fairness
 * - Prevents memory leaks by cleaning up vanish state
 */
class VanishListener : Listener {

    /**
     * Handles player join events.
     * - Hides all vanished players from the joining player (unless they have permission to see them)
     * - Ensures vanished players are not visible to regular players
     *
     * @param event The PlayerJoinEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        VanishManager.handlePlayerJoin(event.player)
    }

    /**
     * Handles player quit events.
     * - Unvanishes the quitting player
     * - Cleans up all vanish-related data to prevent memory leaks
     * - Ensures vanished state doesn't persist across sessions
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        VanishManager.handlePlayerQuit(event.player)
    }

    /**
     * Handles player death events.
     * - Auto-unvanishes players when they die
     * - Ensures vanished players can't exploit death mechanics
     * - Maintains game fairness
     *
     * @param event The PlayerDeathEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        // If a vanished player dies, unvanish them
        if (VanishManager.isVanished(player)) {
            VanishManager.unvanish(player)
        }
    }
}