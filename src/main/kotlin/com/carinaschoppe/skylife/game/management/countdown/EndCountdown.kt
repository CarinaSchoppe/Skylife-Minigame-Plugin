package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.Bukkit

class EndCountdown(game: Game) : Countdown(game) {
    override val defaultDuration: Int = 10


    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {
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