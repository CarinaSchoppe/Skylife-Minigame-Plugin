package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.countdown.Countdown
import com.carinaschoppe.skylife.game.management.countdown.IngameCountdown

class IngameState(game: Game) : GameState(game) {
    override val gameStateID: Int = GameStates.INGAME_STATE.id


    override val countdown: Countdown = IngameCountdown(game)

    override fun start() {
        game.currentState = this
        TODO("Not yet implemented")
    }

    override fun stop() {
        game.gameStats[game.gameStats.indexOf(this) + 1].start()
    }
}