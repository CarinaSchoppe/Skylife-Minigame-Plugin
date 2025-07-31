package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.countdown.Countdown
import com.carinaschoppe.skylife.game.countdown.IngameCountdown
import com.carinaschoppe.skylife.game.countdown.ProtectionCountdown
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.game.managers.GameManager
import com.carinaschoppe.skylife.game.managers.MapManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility

class IngameState(game: Game) : GameState(game) {
    override val gameStateID: Int = GameStates.INGAME_STATE.id


    override val countdown: Countdown = IngameCountdown(game)
    private val protectionCountdown: Countdown = ProtectionCountdown(game)

    override fun start() {
        game.currentState = this

        GameCluster.lobbyGames.remove(game)
        GameCluster.activeGames.add(game)

        countdown.start()
        protectionCountdown.start()


        //TODO: is something missing in this method?

        hideSpectators()
        //teleport all players
        val locations = game.gamePattern.gameLocationManager.spawnLocations.toTypedArray()

        for (i in 0 until game.livingPlayers.size) {
            //Translate location
            game.livingPlayers[i].sendMessage(Messages.TELEPORT)
            game.livingPlayers[i].teleport(MapManager.locationWorldConverter(GameLocationManager.skylifeLocationToLocationConverter(locations[i]), game))
            StatsUtility.addStatsToPlayerWhenJoiningGame(game.livingPlayers[i])
            game.livingPlayers[i].sendMessage(Messages.INGAME_START)
            game.livingPlayers[i].sendMessage(Messages.MAP_NAME(game.gamePattern.mapName))
            game.livingPlayers[i].sendMessage(Messages.TELEPORT)

            //TODO add Kit functionality
        }

        game.spectators.forEach {
            it.sendMessage(Messages.INGAME_START)
            it.sendMessage(Messages.TELEPORT)
            it.sendMessage(Messages.MAP_NAME(game.gamePattern.mapName))
            it.teleport(MapManager.locationWorldConverter(GameLocationManager.skylifeLocationToLocationConverter(game.gamePattern.gameLocationManager.spectatorLocation), game))
        }

        GameManager.checkGameOver(game)
    }


    //hide specators

    fun hideSpectators() {
        game.livingPlayers.forEach { living ->
            game.spectators.forEach { spectator ->
                living.hidePlayer(Skylife.instance, spectator)
            }
        }
    }

    override fun stop() {
        game.gameStats[game.gameStats.indexOf(this) + 1].start()
    }
}