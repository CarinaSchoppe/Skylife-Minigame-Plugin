package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit

class ProtectionCountdown(game: Game) : Countdown(game) {

    override val defaultDuration: Int = 10
    private fun message() {
        game.livingPlayers.forEach { it.sendMessage(Messages.PROTECTION_TIME(duration)) }
        game.spectators.forEach { it.sendMessage(Messages.PROTECTION_TIME(duration)) }
    }

    override fun start() {
        countdown = Bukkit.getScheduler().runTaskTimer(Skylife.instance, Runnable {

            if (game.currentState !is IngameState) {
                stop()
            }

            duration--
            when (duration) {
                in 10..1 -> message()
                0 -> stop()
            }
        }, 0, 20)
    }

    override fun stop() {
        game.livingPlayers.forEach { it.sendMessage(Messages.PROTECTION_ENDS) }
        game.spectators.forEach { it.sendMessage(Messages.PROTECTION_ENDS) }
        countdown.cancel()
    }
}