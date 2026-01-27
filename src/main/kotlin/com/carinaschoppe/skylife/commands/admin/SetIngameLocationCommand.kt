package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Handles the command to set various important locations for a game pattern.
 * These locations are used to teleport players during different phases of the game.
 *
 * Command Usage:
 * - `/setlocation <mapName> <lobby|spawn|spectator|main>`
 */
class SetIngameLocationCommand : CommandExecutor, TabCompleter {

    /**
     * Executes the location setting command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("setlocation", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (args.size != 2) {
            sender.sendMessage(Messages.ERROR_ARGUMENT)
            return true
        }

        val mapName = args[0]
        val type = args[1].lowercase()

        val game = GameCluster.gamePatterns.firstOrNull { it.mapName.equals(mapName, ignoreCase = true) }
        if (game == null) {
            sender.sendMessage(Messages.ERROR_NO_PATTERN)
            return true
        }

        when (type) {
            "lobby" -> {
                if (!sender.hasPermission("skylife.setlocation.lobby")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                game.gameLocationManager.lobbyLocation = GameLocationManager.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.LOCATION_ADDED("lobby", game.mapName))
            }

            "spawn" -> {
                if (!sender.hasPermission("skylife.setlocation.spawn")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                game.gameLocationManager.spawnLocations.add(GameLocationManager.locationToSkylifeLocationConverter(sender.location))
                sender.sendMessage(Messages.LOCATION_ADDED("spawn", game.mapName, game.gameLocationManager.spawnLocations.size))
            }

            "spectator" -> {
                if (!sender.hasPermission("skylife.setlocation.spectator")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                game.gameLocationManager.spectatorLocation = GameLocationManager.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.LOCATION_ADDED("spectator", game.mapName))
            }

            "main" -> {
                if (!sender.hasPermission("skylife.setlocation.main")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                game.gameLocationManager.mainLocation = GameLocationManager.locationToSkylifeLocationConverter(sender.location)
                sender.sendMessage(Messages.LOCATION_ADDED("main", game.mapName))
            }

            else -> {
                sender.sendMessage(Messages.ERROR_ARGUMENT)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> GameCluster.gamePatterns
                .map { it.mapName }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }

            2 -> listOf("lobby", "spawn", "spectator", "main")
                .filter { it.startsWith(args[1].lowercase()) }

            else -> emptyList()
        }
    }
}