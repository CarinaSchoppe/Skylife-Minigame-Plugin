package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.events.player.PlayerDisplayNameListener.Companion.updatePlayerDisplayName
import com.carinaschoppe.skylife.hub.HubManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.scoreboard.LobbyScoreboardManager
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
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

        // Ensure the player has a statistics entry. This should handle first-time joins internally.
        StatsUtility.addStatsPlayerWhenFirstJoin(player)

        // --- Reset Player State for Lobby ---
        player.gameMode = GameMode.ADVENTURE
        player.health = 20.0
        player.foodLevel = 20

        // Clear inventory and armor
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4) // Empty armor array (4 slots: boots, leggings, chestplate, helmet)

        if (player.hasPermission("skylife.overview")) {
            player.inventory.setItem(0, GameOverviewItems.createMenuItem())
        }

        // Clear all active effects
        player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

        // Update player display name with guild tag if applicable
        updatePlayerDisplayName(player)

        // Teleport to hub spawn
        HubManager.teleportToHub(player)

        // Set lobby scoreboard
        LobbyScoreboardManager.setLobbyScoreboard(player)

        // Broadcast join message with online player count and max players
        val server = player.server
        player.server.broadcast(Messages.PLAYER_JOINED(player.name, server.onlinePlayers.size, server.maxPlayers))

        // Send a custom welcome message.
        player.sendMessage(Messages.PLAYER_JOINS_SERVER(player.name))
    }
}
