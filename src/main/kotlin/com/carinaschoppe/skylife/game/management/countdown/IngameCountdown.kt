package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.Bukkit

class IngameCountdown(game: Game) : Countdown(game) {

    override val defaultDuration: Long = 15


    override fun start() {
        countdown = Bukkit.getScheduler().runTaskLater(Skylife.instance, Runnable {
            stop()
        }, duration * 20 * 60)
    }

    override fun stop() {
        game.currentState.stop()
    }
}