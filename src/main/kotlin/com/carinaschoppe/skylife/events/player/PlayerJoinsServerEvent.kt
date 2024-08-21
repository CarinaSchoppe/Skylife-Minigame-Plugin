package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinsServerEvent : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
        StatsUtility.addStatsPlayerWhenFirstJoin(event.player)
        event.player.sendMessage(Messages.PLAYER_JOINS_SERVER(event.player.name))

    }


}