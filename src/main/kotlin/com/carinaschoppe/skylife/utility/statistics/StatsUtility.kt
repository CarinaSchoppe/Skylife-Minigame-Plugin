package com.carinaschoppe.skylife.utility.statistics

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object StatsUtility {


    fun loadAllPlayersIntoStatsPlayer() {
        transaction {
            statsPlayers.addAll(StatsPlayer.all())
        }
        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Stats loaded!", Messages.MESSAGE_COLOR)))
    }

    fun loadStatsPlayerWhenFirstJoin(player: Player) {
        transaction {
            statsPlayers.add(StatsPlayer[player.uniqueId.toString()])
        }
    }

    fun addStatsToPlayerWhenLeave(player: Player) {
        //get player from Database
        transaction {
            val statsPlayer = statsPlayers.first { it.uuid == player.uniqueId.toString() }
            if (GameCluster.activeGames.any { it.livingPlayers.contains(player) && it.currentState is IngameState }) {
                statsPlayer.deaths
            }
        }
    }


    fun addWinStatsToPlayer(player: Player) {
        transaction {
            val statsPlayer = statsPlayers.first { it.uuid == player.uniqueId.toString() }

            statsPlayer.wins++

        }
    }

    fun addDeathStatsToPlayer(player: Player) {
        transaction {
            val statsPlayer = statsPlayers.first { it.uuid == player.uniqueId.toString() }

            statsPlayer.deaths++

        }
    }

    fun addKillStatsToPlayer(player: Player) {
        transaction {
            val statsPlayer = statsPlayers.first { it.uuid == player.uniqueId.toString() }

            statsPlayer.kills++

        }
    }

    fun addStatsPlayerWhenFirstJoin(player: Player) {
        //add stats
        transaction {
            val statsPlayer: StatsPlayer = statsPlayers.firstOrNull { it.uuid == player.uniqueId.toString() } ?: StatsPlayer.new {
                uuid = player.uniqueId.toString()
                kills = 0
                name = player.name
                deaths = 0
                wins = 0
                games = 0
            }

            statsPlayers.add(statsPlayer)

        }

    }

    fun addStatsToPlayerWhenJoiningGame(player: Player) {
        val statsPlayer = statsPlayers.first { it.uuid == player.uniqueId.toString() }
        transaction {
            statsPlayer.games++
        }
    }

    val statsPlayers = mutableSetOf<StatsPlayer>()

}