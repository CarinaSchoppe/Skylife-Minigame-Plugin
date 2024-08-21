package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetLocationCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "setlocation") return false

        if (args == null) {
            sender.sendMessage(Messages.ERROR_ARGUMENT)
            return false
        }


        if (args.size != 2) {
            sender.sendMessage(Messages.ERROR_ARGUMENT)
            return false
        }


        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return false
        }

        val type = args[1]

        val game = try {
            GameCluster.gamePatterns.first { it.mapName == args[0] }
        } catch (e: Exception) {

            //TODO: sendmessage
            return false
        }

        when (type) {
            "lobby" -> {
                if (!sender.hasPermission("skylife.setlocation.lobby")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManagement.lobbyLocation = sender.location
                sender.sendMessage(Messages.LOCATION_ADDED)
            }

            "spawn" -> {
                if (!sender.hasPermission("skylife.setlocation.spawn")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManagement.spawnLocations.add(sender.location)
                sender.sendMessage(Messages.LOCATION_ADDED)
            }

            "spectator" -> {
                if (!sender.hasPermission("skylife.setlocation.spectator")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManagement.spectatorLocation = sender.location
                sender.sendMessage(Messages.LOCATION_ADDED)
            }
        }
        return false
    }
}