package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class PlayerPlacesBlockEvent : Listener {


    @EventHandler(ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val game = GameCluster.lobbyGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: GameCluster.activeGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: run {
            event.isCancelled = true
            event.player.sendMessage(Messages.instance.CANT_BREAK_BLOCK)
            return
        }

        if (game.currentState !is IngameState) {
            event.player.sendMessage(Messages.instance.CANT_BREAK_BLOCK)
            event.isCancelled = true
            return
        }
    }


}