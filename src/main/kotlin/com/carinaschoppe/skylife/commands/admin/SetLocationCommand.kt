package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetLocationCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "setlocation") return false

        if (args == null) {
            return false
            //TODO: send message
        }


        if (args.size != 2) {
            return false
            //TODO: send message
        }


        if (sender !is Player) {
            return false
            //TODO: send message
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
                    return false
                    //TODO:send message
                }
                game.gameLocationManagement.lobbyLocation = sender.location
                //TODO: message
            }

            "spawn" -> {
                if (!sender.hasPermission("skylife.setlocation.spawn")) {
                    return false
                    //TODO: send message
                }
                game.gameLocationManagement.spawnLocations.add(sender.location)
                //TODO: message
            }

            "spectator" -> {
                if (!sender.hasPermission("skylife.setlocation.spectator")) {
                    return false
                    //TODO: send message
                }
                game.gameLocationManagement.spectatorLocation = sender.location
                //TODO: message
            }
        }
        return false
    }
}