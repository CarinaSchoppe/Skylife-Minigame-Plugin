package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
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


        //adding stats
        game.livingPlayers.forEach { StatsUtility.addStatsToPlayerWhenJoiningGame(it) }

        hideSpectators()
        //teleport all players
        val locations = game.gamePattern.gameLocationManagement.spawnLocations.toTypedArray()

        for (i in 0 until game.livingPlayers.size) {
            //Translate location
            game.livingPlayers.toTypedArray()[i].teleport(Utility.locationWorldConverter(locations[i], game))

            //TODO: message here
            game.livingPlayers.toTypedArray()[i].sendMessage(Messages.INGAME_START())
        }

        game.spectators.forEach {
            it.teleport(Utility.locationWorldConverter(game.gamePattern.gameLocationManagement.spectatorLocation, game))
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