package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.utility.configuration.Configurations
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit

class EndCountdown(game: Game) : Countdown(game, Configurations.instance.END_TIMER) {

    private fun message() {
        game.livingPlayers.forEach { it.sendMessage(Messages.instance.GAME_END_TIMER(duration)) }
        game.spectators.forEach { it.sendMessage(Messages.instance.GAME_END_TIMER(duration)) }
    }

    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {

            if (game.livingPlayers.isEmpty() && game.spectators.isEmpty())
                stop()

            duration--



            when (duration) {
                60, 45, 30, 15 -> {
                    message()
                }

                in 1..10 -> {
                    message()
                }

                0 -> stop()
            }
        }, 0, 20)
    }

    override fun stop() {
        countdown.cancel()
        game.currentState.stop()
    }

}