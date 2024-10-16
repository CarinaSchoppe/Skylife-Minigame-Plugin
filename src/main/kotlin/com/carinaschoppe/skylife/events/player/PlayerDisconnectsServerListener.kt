package com.carinaschoppe.skylife.events.player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerDisconnectsServerListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.performCommand("leave")
        event.quitMessage(null)
    }


}