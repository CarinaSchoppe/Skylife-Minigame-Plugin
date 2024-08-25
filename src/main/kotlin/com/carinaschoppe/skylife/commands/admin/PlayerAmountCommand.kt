package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerAmountCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "playeramount") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.instance.ERROR_NOTPLAYER)
            return false
        }

        if (args == null) {
            sender.sendMessage(Messages.instance.ERROR_ARGUMENT)
            return false
        }


        if (args.size != 3) {
            sender.sendMessage(Messages.instance.ERROR_ARGUMENT)
            return false
        }


        val game = try {
            GameCluster.gamePatterns.first { it.mapName == args[0] }
        } catch (e: Exception) {

            sender.sendMessage(Messages.instance.ERROR_COMMAND)
            return false
        }
        val type = args[1]
        val amount = args[2] as Int


        if (type == "min") {
            if (!sender.hasPermission("skylife.playeramount.min")) {
                sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                return false
            }
            game.minPlayers = amount
            sender.sendMessage(Messages.instance.PLAYER_AMOUNT_SET)
        } else if (type == "max") {

            if (!sender.hasPermission("skylife.playeramount.min")) {
                sender.sendMessage(Messages.instance.ERROR_PERMISSION)
                return false
            }
            game.maxPlayers = amount
            sender.sendMessage(Messages.instance.PLAYER_AMOUNT_SET)
        }


        return false
    }
}