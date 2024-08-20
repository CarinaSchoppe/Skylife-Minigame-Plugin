package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.countdown.Countdown

abstract class GameState(val game: Game) {


    abstract val countdown: Countdown

    abstract val gameStateID: Int

    abstract fun start()

    abstract fun stop()


}