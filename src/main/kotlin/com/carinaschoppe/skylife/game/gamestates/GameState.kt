package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.countdown.Countdown

abstract class GameState(val game: Game) {


    abstract val countdown: Countdown

    abstract val gameStateID: Int

    abstract fun start()

    abstract fun stop()


}