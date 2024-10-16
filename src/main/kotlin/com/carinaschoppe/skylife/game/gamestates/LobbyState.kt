package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.countdown.Countdown
import com.carinaschoppe.skylife.game.countdown.LobbyCountdown

class LobbyState(game: Game) : GameState(game) {

    override fun start() {
        game.currentState = this
        countdown.start()
    }

    override val countdown: Countdown = LobbyCountdown(game)


    override fun stop() {
        game.gameStats[game.gameStats.indexOf(this) + 1].start()
    }

    override val gameStateID: Int = GameStates.LOBBY_STATE.id


}