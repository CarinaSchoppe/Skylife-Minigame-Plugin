package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.GamePattern
import com.carinaschoppe.skylife.game.miscellaneous.GameLoader
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateGamePatternCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "game") return false

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

        val type = args[0]
        val name = args[1]


        when (type) {
            "create" -> {
                if (!sender.hasPermission("skylife.create")) {
                    return false
                    //TODO: send message
                }
                if (GameCluster.gamePatterns.any { it.mapName == name }) {
                    //todo: send message
                    return false
                }
                //create a new gamepattern
                var pattern = GamePattern(name)
                GameCluster.gamePatterns.add(pattern)
                //TODO: message
            }

            "save" -> {
                if (!sender.hasPermission("skylife.save")) {
                    return false
                    //TODO: send message
                }


                //check if any pattern with that name exists
                if (GameCluster.gamePatterns.any { it.mapName == name }) {

                    val game = GameCluster.gamePatterns.first { it.mapName == name }
                    GameLoader.saveGameToFile(game)
                    //TODO: send message
                }
            }


            "delete" -> {
                if (!sender.hasPermission("skylife.delete")) {
                    return false
                    //TODO: send message
                }
                //check if any pattern with that name exists
                if (GameCluster.gamePatterns.any { it.mapName == name }) {

                    val game = GameCluster.gamePatterns.first { it.mapName == name }
                    GameCluster.gamePatterns.remove(game)
                    GameLoader.deleteGameFile(game)
                    //TODO: send message
                }
            }
        }






        return false


    }


}