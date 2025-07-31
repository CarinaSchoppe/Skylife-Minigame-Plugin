package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.countdown.LobbyCountdown
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class QuickstartGameCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.label != "start") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return false
        }
        if (!sender.hasPermission("skylife.start")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return false
        }
        val game: Game = try {
            GameCluster.lobbyGames.first { it.livingPlayers.contains(sender) }
        } catch (e: Exception) {
            sender.sendMessage(Messages.NOT_INGAME)
            return false
        }

        val countdown = game.currentState.countdown as LobbyCountdown
        countdown.duration = if (countdown.duration > 5) {
            game.livingPlayers.forEach { it.sendMessage(Messages.ROUND_SPEED_ALL) }
            game.spectators.forEach { it.sendMessage(Messages.ROUND_SPEED_ALL) }
            5
        } else {
            sender.sendMessage(Messages.ROUND_SPEED_LOW)
            countdown.duration

        }


        return false
    }


}