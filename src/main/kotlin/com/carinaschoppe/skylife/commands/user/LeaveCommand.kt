package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages.Companion.instance
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "leave") return false
        if (sender !is Player) {
            sender.sendMessage(instance.ERROR_NOTPLAYER)
            return false
        }

        if (!sender.hasPermission("skylife.leave")) {
            sender.sendMessage(instance.ERROR_PERMISSION)
            return false
        }

        GameCluster.removePlayerFromGame(sender)
        sender.sendMessage(instance.OWN_PLAYER_LEFT)
        StatsUtility.addStatsToPlayerWhenLeave(sender)

        return false
    }


}