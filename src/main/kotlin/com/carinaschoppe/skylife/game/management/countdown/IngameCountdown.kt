package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.gamestates.EndState
import org.bukkit.Bukkit

class IngameCountdown(game: Game) : Countdown(game) {

    override val defaultDuration: Int = 15 * 60


    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {

            if (game.currentState is EndState)
                countdown.cancel()

            duration--
            when (duration) {
                0 -> stop()

            }


        }, 0, 20)
    }

    override fun stop() {
        countdown.cancel()
        game.currentState.stop()
    }
}