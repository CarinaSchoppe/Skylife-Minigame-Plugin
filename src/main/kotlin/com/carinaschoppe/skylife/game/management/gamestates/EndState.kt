package com.carinaschoppe.skylife.game.management.gamestates

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.countdown.Countdown
import com.carinaschoppe.skylife.game.management.countdown.EndCountdown
import com.carinaschoppe.skylife.game.miscellaneous.Utility
import com.carinaschoppe.skylife.utility.statistics.StatsUtility

class EndState(game: Game) : GameState(game) {

    override val gameStateID: Int = GameStates.END_STATE.id
    override val countdown: Countdown = EndCountdown(game)

    override fun start() {
        game.currentState = this


        countdown.start()
        Utility.endingMatchMessage(game)
        //add winning Stats to Player
        if (game.livingPlayers.size == 1)
            StatsUtility.addWinStatsToPlayer(game.livingPlayers.firstOrNull() ?: return)
    }


    override fun stop() {
        game.cancel()

    }


}