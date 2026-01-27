package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.party.PartyManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listens for players disconnecting to clean up party data.
 */
class PlayerDisconnectsPartyListener : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        PartyManager.handlePlayerDisconnect(player.uniqueId)
    }
}
