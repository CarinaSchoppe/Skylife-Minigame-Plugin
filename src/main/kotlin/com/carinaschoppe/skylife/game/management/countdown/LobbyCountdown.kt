package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit

class LobbyCountdown(game: Game) : Countdown(game) {
    override val defaultDuration: Int = 60

    var idle = false
    private fun message() {
        game.livingPlayers.forEach { it.sendMessage(Messages.LOBBY_TIMER(duration)) }
        game.spectators.forEach { it.sendMessage(Messages.LOBBY_TIMER(duration)) }
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
                in 9..1 -> message()
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