package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.gamestates.EndState
import com.carinaschoppe.skylife.utility.configuration.Timer
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit

class IngameCountdown(game: Game) : Countdown(game, Timer.instance.INGAME_TIMER) {

    private fun message() {
        game.livingPlayers.forEach { it.sendMessage(Messages.GAME_END_TIMER(duration)) }
        game.spectators.forEach { it.sendMessage(Messages.GAME_END_TIMER(duration)) }
    }

    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {

            if (game.currentState is EndState)
                countdown.cancel()
            duration--
            when (duration) {

                900 -> message()

                600 -> message()

                300 -> message()

                in 240 downTo 60 step 60 -> {
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