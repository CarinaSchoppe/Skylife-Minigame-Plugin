package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.GameLocationManagement
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetLocationCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "setlocation") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.instance.ERROR_NOTPLAYER)
            return false
        }


        if (!sender.hasPermission("skylife.setlocation")) {
            sender.sendMessage(Messages.instance.ERROR_PERMISSION)
            return false
        }

        if (args == null) {
            sender.sendMessage(Messages.instance.ERROR_ARGUMENT)
            return false
        }


        if (args.size != 2) {
            sender.sendMessage(Messages.instance.ERROR_ARGUMENT)
            return false
        }


        val type = args[1]

        val game = try {
            GameCluster.gamePatterns.first { it.mapName == args[0] }
        } catch (e: Exception) {
            sender.sendMessage(Messages.instance.ERROR_NO_PATTERN)
            return false
        }

        when (type) {
            "lobby" -> {
                if (!sender.hasPermission("skylife.setlocation.lobby")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManagement.lobbyLocation = GameLocationManagement.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.instance.LOCATION_ADDED("lobby", game.mapName))
            }

            "spawn" -> {
                if (!sender.hasPermission("skylife.setlocation.spawn")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManagement.spawnLocations.add(GameLocationManagement.locationToSkylifeLocationConverter(sender.location))
                sender.sendMessage(Messages.instance.LOCATION_ADDED("spawn", game.mapName, game.gameLocationManagement.spawnLocations.size))
            }

            "spectator" -> {
                if (!sender.hasPermission("skylife.setlocation.spectator")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManagement.spectatorLocation = GameLocationManagement.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.instance.LOCATION_ADDED("spectator", game.mapName))
            }
        }
        return false
    }
}