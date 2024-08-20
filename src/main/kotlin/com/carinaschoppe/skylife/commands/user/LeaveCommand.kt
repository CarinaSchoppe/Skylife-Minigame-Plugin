package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.management.GameCluster
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "start") return false
        if (sender !is Player)
            return false

        if (!sender.hasPermission("skylife.leave")) {
            //TODO: send message
            return false
        }

        GameCluster.removePlayerFromGame(sender)


        return false
    }
}