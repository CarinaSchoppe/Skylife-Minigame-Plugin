package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.Bukkit

class ProtectionCountdown(game: Game) : Countdown(game) {

    override val defaultDuration: Long = 10

    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {
            duration -= 1
            when (duration) {
                0L -> stop()
            }
        }, 0, 20)
    }

    override fun stop() {
    }
}