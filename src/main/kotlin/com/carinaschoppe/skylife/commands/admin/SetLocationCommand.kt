package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetLocationCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.label != "setlocation") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.instance.ERROR_NOTPLAYER)
            return false
        }


        if (!sender.hasPermission("skylife.setlocation")) {
            sender.sendMessage(Messages.instance.ERROR_PERMISSION)
            return false
        }

        if (args.isEmpty()) {
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
                game.gameLocationManager.lobbyLocation = GameLocationManager.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.instance.LOCATION_ADDED("lobby", game.mapName))
            }

            "spawn" -> {
                if (!sender.hasPermission("skylife.setlocation.spawn")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManager.spawnLocations.add(GameLocationManager.locationToSkylifeLocationConverter(sender.location))
                sender.sendMessage(Messages.instance.LOCATION_ADDED("spawn", game.mapName, game.gameLocationManager.spawnLocations.size))
            }

            "spectator" -> {
                if (!sender.hasPermission("skylife.setlocation.spectator")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManager.spectatorLocation = GameLocationManager.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.instance.LOCATION_ADDED("spectator", game.mapName))
            }

            "main" -> {
                if (!sender.hasPermission("skylife.setlocation.main")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                game.gameLocationManager.mainLocation = GameLocationManager.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.instance.LOCATION_ADDED("main", game.mapName))
            }
        }
        return false
    }
}