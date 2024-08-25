package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.GameLocationManagement
import com.carinaschoppe.skylife.game.management.countdown.Countdown
import com.carinaschoppe.skylife.game.management.countdown.IngameCountdown
import com.carinaschoppe.skylife.game.management.countdown.ProtectionCountdown
import com.carinaschoppe.skylife.game.miscellaneous.Utility
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility

class IngameState(game: Game) : GameState(game) {
    override val gameStateID: Int = GameStates.INGAME_STATE.id


    override val countdown: Countdown = IngameCountdown(game)
    val protectionCountdown: Countdown = ProtectionCountdown(game)

    override fun start() {
        game.currentState = this
        countdown.start()
        protectionCountdown.start()


        //TODO: is something missing in this method?

        hideSpectators()
        //teleport all players
        val locations = game.gamePattern.gameLocationManagement.spawnLocations.toTypedArray()

        for (i in 0 until game.livingPlayers.size) {
            //Translate location
            game.livingPlayers[i].sendMessage(Messages.instance.TELEPORT)
            game.livingPlayers[i].teleport(Utility.locationWorldConverter(GameLocationManagement.skylifeLocationToLocationConverter(locations[i]), game))
            StatsUtility.addStatsToPlayerWhenJoiningGame(game.livingPlayers[i])
            game.livingPlayers[i].sendMessage(Messages.instance.INGAME_START)
            game.livingPlayers[i].sendMessage(Messages.instance.MAP_NAME(game.gamePattern.mapName))
            game.livingPlayers[i].sendMessage(Messages.instance.TELEPORT)

            //TODO add Kit functionality
        }

        game.spectators.forEach {
            it.sendMessage(Messages.instance.INGAME_START)
            it.sendMessage(Messages.instance.TELEPORT)
            it.sendMessage(Messages.instance.MAP_NAME(game.gamePattern.mapName))
            it.teleport(Utility.locationWorldConverter(GameLocationManagement.skylifeLocationToLocationConverter(game.gamePattern.gameLocationManagement.spectatorLocation), game))
        }
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