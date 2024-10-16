package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PlayerDamagesListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            val game = GameCluster.lobbyGames.firstOrNull { it.livingPlayers.contains(event.entity) or it.spectators.contains(event.entity) } ?: GameCluster.activeGames.firstOrNull { it.livingPlayers.contains(event.entity) or it.spectators.contains(event.entity) } ?: run {
                event.isCancelled = true
                return
            }
            if (game.currentState !is IngameState) {
                event.isCancelled = true
                return
            }
        }

        if (event.damager is Player) {
            val game = GameCluster.lobbyGames.firstOrNull { it.livingPlayers.contains(event.damager) or it.spectators.contains(event.damager) } ?: GameCluster.activeGames.firstOrNull { it.livingPlayers.contains(event.damager) or it.spectators.contains(event.damager) } ?: run {
                event.isCancelled = true
                event.damager.sendMessage(Messages.instance.CANT_DAMAGE)
                return
            }

            if (game.currentState !is IngameState) {
                event.damager.sendMessage(Messages.instance.CANT_DAMAGE)
                event.isCancelled = true
                return
            }
        }
    }


}