package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinsServerListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
        StatsUtility.addStatsPlayerWhenFirstJoin(event.player)
        event.player.gameMode = GameMode.ADVENTURE
        event.player.sendMessage(Messages.instance.PLAYER_JOINS_SERVER(event.player.name))
        if (StatsUtility.statsPlayers.find { it.uuid == event.player.uniqueId.toString() } == null) {
            StatsUtility.addStatsPlayerWhenFirstJoin(event.player)
        }
    }


}