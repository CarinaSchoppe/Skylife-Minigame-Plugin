package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "start") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return false
        }

        if (!sender.hasPermission("skylife.leave")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return false
        }

        GameCluster.removePlayerFromGame(sender)

        StatsUtility.addStatsToPlayerWhenLeave(sender)

        return false
    }


}