package com.carinaschoppe.skylife.game.gamestates

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.countdown.Countdown
import com.carinaschoppe.skylife.game.countdown.EndCountdown
import com.carinaschoppe.skylife.game.managers.GameManager
import com.carinaschoppe.skylife.utility.statistics.StatsUtility

class EndState(game: Game) : GameState(game) {

    override val gameStateID: Int = GameStates.END_STATE.id
    override val countdown: Countdown = EndCountdown(game)

    override fun start() {
        game.currentState = this


        countdown.start()
        GameManager.endingMatchMessage(game)
        //add winning Stats to Player
        if (game.livingPlayers.size == 1)
            StatsUtility.addWinStatsToPlayer(game.livingPlayers.firstOrNull() ?: return)
    }


    override fun stop() {
        game.cancel()

    }


}