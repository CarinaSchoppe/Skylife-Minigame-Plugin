package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.countdown.Countdown
import com.carinaschoppe.skylife.game.management.countdown.IngameCountdown
import com.carinaschoppe.skylife.game.management.countdown.ProtectionCountdown
import com.carinaschoppe.skylife.utility.messages.Messages

class IngameState(game: Game) : GameState(game) {
    override val gameStateID: Int = GameStates.INGAME_STATE.id


    override val countdown: Countdown = IngameCountdown(game)
    val protectionCountdown: Countdown = ProtectionCountdown(game)

    override fun start() {
        game.currentState = this
        countdown.start()
        protectionCountdown.start()


        //TODO: stats to new living players

        //teleport all players
        val locations = game.gamePattern.gameLocationManagement.spawnLocations.toTypedArray()

        for (i in 0 until game.livingPlayers.size) {
            game.livingPlayers.toTypedArray()[i].teleport(locations[i])

            //TODO: message here
            game.livingPlayers.toTypedArray()[i].sendMessage(Messages.INGAME_START())
        }
    }

    override fun stop() {
        game.gameStats[game.gameStats.indexOf(this) + 1].start()
    }
}