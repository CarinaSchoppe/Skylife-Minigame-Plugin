package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.countdown.LobbyCountdown
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StartCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "start") return false
        if (sender !is Player)
            return false

        if (!sender.hasPermission("skylife.start"))
            return false

        val game: Game = try {
            GameCluster.lobbyGames.first { it.livingPlayers.contains(sender) }
        } catch (e: Exception) {

            //no game found
            //TODO: send message that not exists
            return false
        }

        val countdown = game.currentState.countdown as LobbyCountdown
        countdown.duration = if (countdown.duration > 5) {
            //TODO send message
            5

        } else {

            //TODO: send message
            countdown.duration
        }


        return false
    }


}