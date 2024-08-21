package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StatsCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "vanish") return false

        if (sender !is Player)
            return false

        if (!sender.hasPermission("skylife.vanish")) {
            //TODO: send message
            return false
        }

        if (args == null) {
            //show own stats
            val statsPlayer = StatsUtility.statsPlayers.first { it.uuid == sender.player.uniqueId.toString() }
            //TODO: send message


        } else if (args.size != 1) {

            sender.sendMessage(Messages.ERROR_ARGUMENT)
            return false
        }
        val player = args?.get(0)
        val statsPlayer = StatsUtility.statsPlayers.firstOrNull { it.name == player } ?: run {
            sender.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND)
            return false
        }
        //TODO: send stats message of other player


        return false
    }
}