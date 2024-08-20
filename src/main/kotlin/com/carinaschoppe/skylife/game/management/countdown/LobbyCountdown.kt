package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.Bukkit

class LobbyCountdown(game: Game) : Countdown(game) {
    override val defaultDuration: Long = 60

    var idle = false

    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {
            idle = game.livingPlayers.size < game.gamePattern.minPlayers
            if (idle) {
                duration = defaultDuration
                return@Runnable
            }
            duration -= 1
            when (duration) {
                0L -> stop()
            }
        }, 0, 20)
    }

    override fun stop() {
        game.currentState.stop()
    }
}