package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.countdown.Countdown
import com.carinaschoppe.skylife.game.management.countdown.EndCountdown

class EndState(game: Game) : GameState(game) {

    override val gameStateID: Int = GameStates.END_STATE.id

    override fun start() {
        game.currentState = this
    }


    override val countdown: Countdown = EndCountdown(game)

    override fun stop() {
        game.cancel()

    }


}