package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.countdown.Countdown
import com.carinaschoppe.skylife.game.management.countdown.LobbyCountdown

class LobbyState(game: Game) : GameState(game) {

    override fun start() {
        game.currentState = this
        countdown.start()
    }

    override val countdown: Countdown = LobbyCountdown(game)


    override fun stop() {
        game.gameStats.get(game.gameStats.indexOf(this) + 1).start()
    }

    override val gameStateID: Int = GameStates.LOBBY_STATE.id


}