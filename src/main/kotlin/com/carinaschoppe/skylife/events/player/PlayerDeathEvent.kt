package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.GameLocationManagement
import com.carinaschoppe.skylife.game.miscellaneous.Utility
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeathEvent : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage(null)

        val game = GameCluster.lobbyGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: GameCluster.activeGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: run {
            return
        }

        game.livingPlayers.remove(event.player)
        game.spectators.add(event.player)


        //respawn player
        Bukkit.getScheduler().runTaskLater(Skylife.instance, Runnable {
            event.player.spigot().respawn()
            event.player.gameMode = GameMode.SPECTATOR
            event.player.teleport(Utility.locationWorldConverter(GameLocationManagement.skylifeLocationToLocationConverter(game.gamePattern.gameLocationManagement.spectatorLocation), game))
        }, 1L)


        StatsUtility.addDeathStatsToPlayer(event.player)

        //killed by player do stats and messages
        if (event.damageSource.causingEntity is Player) {
            val killer = event.damageSource.causingEntity as Player
            game.livingPlayers.forEach {
                it.sendMessage(Messages.instance.PLAYER_KILLED(event.player.name, killer.name))
                it.sendMessage(Messages.instance.PLAYERS_REMAINING(game.livingPlayers.size))
            }
            game.spectators.forEach {
                it.sendMessage(Messages.instance.PLAYER_KILLED(event.player.name, killer.name))
                it.sendMessage(Messages.instance.PLAYERS_REMAINING(game.livingPlayers.size))
            }
            StatsUtility.addKillStatsToPlayer(killer)
        } else {
            game.livingPlayers.forEach {
                it.sendMessage(Messages.instance.PLAYER_DIED(event.player.name))
                it.sendMessage(Messages.instance.PLAYERS_REMAINING(game.livingPlayers.size))
            }
            game.spectators.forEach {
                it.sendMessage(Messages.instance.PLAYER_DIED(event.player.name))
                it.sendMessage(Messages.instance.PLAYERS_REMAINING(game.livingPlayers.size))
            }

        }

    }


}