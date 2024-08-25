package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "join") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.instance.ERROR_NOTPLAYER)
            return false
        }


        //Check if player is allready in a game
        GameCluster.lobbyGames.firstOrNull { it.livingPlayers.contains(sender) or it.spectators.contains(sender) } ?: GameCluster.activeGames.firstOrNull { it.livingPlayers.contains(sender) or it.spectators.contains(sender) } ?: run {
            sender.sendMessage(Messages.instance.ALLREADY_IN_GAME)
            return false
        }


        if (!sender.hasPermission("skylife.join")) {
            sender.sendMessage(Messages.instance.ERROR_PERMISSION)
            return false
        }
        if (args == null) {
            //Create args and add "random" to it
            if (sender.hasPermission("skylife.join.random"))
                GameCluster.addPlayerToRandomGame(sender)
            return false
        }
        if (args.size != 1) {
            sender.sendMessage(Messages.instance.ERROR_ARGUMENT)
            return false
        }
        if (args[0] == "random") {
            if (sender.hasPermission("skylife.join.random"))
                GameCluster.addPlayerToRandomGame(sender)
            else {
                sender.sendMessage(Messages.instance.ERROR_PERMISSION)

            }
        } else {
            if (sender.hasPermission("skylife.join.map")) {
                val mapName = args[0]
                if (GameCluster.gamePatterns.any { it.mapName == mapName }) {
                    GameCluster.addPlayerToGame(sender, mapName)
                } else {
                    sender.sendMessage(Messages.instance.GAME_NOT_EXISTS(mapName))
                }
            } else {
                sender.sendMessage(Messages.instance.ERROR_PERMISSION)
            }
        }

        return false
    }


    //fun addStatsToPlayer

}