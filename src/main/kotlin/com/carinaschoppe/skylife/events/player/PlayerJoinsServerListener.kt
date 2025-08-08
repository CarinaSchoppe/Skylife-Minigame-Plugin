package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Listener to handle the initial setup for a player joining the server.
 *
 * This listener ensures that every player who joins is in a clean state and ready for the lobby:
 * - Suppresses the default join message.
 * - Creates a statistics entry for the player if it's their first time joining.
 * - Resets the player's gamemode, health, food level, and inventory.
 * - Clears any active potion effects.
 * - Teleports the player to the main hub location.
 * - Broadcasts a custom welcome message.
 */
class PlayerJoinsServerListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // Suppress the default message to use a custom one.
        event.joinMessage(null)

        val player = event.player

        // Load the player's stats into cache, creating a new entry if it's their first join
        StatsUtility.loadStatsPlayerWhenFirstJoin(player)

        // --- Reset Player State for Lobby ---
        player.gameMode = GameMode.ADVENTURE
        player.health = 20.0
        player.foodLevel = 20

        // Clear inventory and armor
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4) // Empty armor array (4 slots: boots, leggings, chestplate, helmet)

        // Clear all active effects
        player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

        // Broadcast join message with online player count and max players
        val server = player.server
        player.server.broadcast(Messages.PLAYER_JOINED(player.name, server.onlinePlayers.size, server.maxPlayers))

        // TODO: Implement a HubManager to get the hub location from a config file.
        // player.teleport(HubManager.getHubLocation())

        // Send a custom welcome message.
        player.sendMessage(Messages.PLAYER_JOINS_SERVER(player.name))
    }
}