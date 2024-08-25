package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.GamePattern
import com.carinaschoppe.skylife.game.miscellaneous.GameLoader
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateGamePatternCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "game") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.instance.ERROR_NOTPLAYER)
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


        val type = args[0]
        val name = args[1]


        when (type) {
            "create" -> {
                if (!sender.hasPermission("skylife.create")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                if (GameCluster.gamePatterns.any { it.mapName == name }) {
                    sender.sendMessage(Messages.instance.ERROR_PATTERN)
                    return false
                }
                //create a new gamepattern
                var pattern = GamePattern(name)
                GameCluster.gamePatterns.add(pattern)
                sender.sendMessage(Messages.instance.GAME_CREATED(name))
            }

            "save" -> {
                if (!sender.hasPermission("skylife.save")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                //check if any pattern with that name exists
                if (GameCluster.gamePatterns.any { it.mapName == name }) {

                    val game = GameCluster.gamePatterns.first { it.mapName == name }

                    if (!game.gameLocationManagement.gamePatternComplete()) {
                        sender.sendMessage(Messages.instance.GAME_PATTERN_NOT_FULLY_DONE(game.mapName))
                        return false
                    }

                    GameLoader.saveGameToFile(game)
                    sender.sendMessage(Messages.instance.GAME_SAVED)
                }
            }

            "delete" -> {
                if (!sender.hasPermission("skylife.delete")) {
                    sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                    return false
                }
                //check if any pattern with that name exists
                if (GameCluster.gamePatterns.any { it.mapName == name }) {

                    val game = GameCluster.gamePatterns.first { it.mapName == name }
                    GameCluster.gamePatterns.remove(game)
                    GameLoader.deleteGameFile(game)
                    sender.sendMessage(Messages.instance.GAME_DELETED)
                }
            }
        }

        return false


    }


}