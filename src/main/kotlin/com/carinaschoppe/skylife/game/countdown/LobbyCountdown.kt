package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.utility.configuration.Configurations
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit

class LobbyCountdown(game: Game) : Countdown(game, Configurations.instance.LOBBY_TIMER) {

    var idle = false
    private fun message() {
        game.livingPlayers.forEach { it.sendMessage(Messages.instance.LOBBY_TIMER(duration)) }
        game.spectators.forEach { it.sendMessage(Messages.instance.LOBBY_TIMER(duration)) }
    }

    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {
            idle = game.livingPlayers.size < game.gamePattern.minPlayers
            if (idle) {
                duration = defaultDuration
                return@Runnable
            }
            when (duration) {
                60, 30, 15, 10 -> message()
                in 1..9 -> message()
                0 -> stop()
            }
            duration--
        }, 0, 20)
    }

    override fun stop() {
        countdown.cancel()
        game.currentState.stop()
    }
}