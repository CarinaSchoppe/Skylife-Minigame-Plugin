package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class PlayerPlacesBlockListener : Listener {


    @EventHandler(ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (GameCluster.lobbyGames.any { game -> game.livingPlayers.contains(event.player) or game.spectators.contains(event.player) } or GameCluster.activeGames.any { game -> game.livingPlayers.contains(event.player) or game.spectators.contains(event.player) }) {
            event.isCancelled = true
            event.player.sendMessage(Messages.CANT_PLACE_BLOCK)
            return
        }
        val game = GameCluster.lobbyGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: GameCluster.activeGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: run {
            return
        }

        if (game.currentState !is IngameState) {
            event.player.sendMessage(Messages.CANT_PLACE_BLOCK)
            event.isCancelled = true
            return
        }
    }


}