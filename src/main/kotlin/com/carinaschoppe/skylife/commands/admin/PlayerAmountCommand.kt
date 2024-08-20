package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerAmountCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "playeramount") return false


        if (args == null) {
            return false
            //TODO: send message
        }


        if (args.size != 3) {
            return false
            //TODO: send message
        }


        if (sender !is Player) {
            return false
            //TODO: send message
        }

        val game = try {
            GameCluster.gamePatterns.first { it.mapName == args[0] }
        } catch (e: Exception) {

            //TODO: sendmessage
            return false
        }
        val type = args[1]
        val amount = args[2] as Int


        if (type == "min") {
            if (!sender.hasPermission("skylife.playeramount.min")) {
                return false
                //TODO:send message
            }
            game.minPlayers = amount
            //TODO: message
        } else if (type == "max") {

            if (!sender.hasPermission("skylife.playeramount.min")) {
                return false
                //TODO: send message
            }
            game.maxPlayers = amount
            //TODO: message
        }


        return false
    }
}