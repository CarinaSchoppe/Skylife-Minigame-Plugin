package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.miscellaneous.Utility
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class VanishCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "vanish") return false

        if (sender !is Player)
            return false

        if (!sender.hasPermission("skylife.vanish")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return false
        }

        if (Utility.vanishedPlayers.contains(sender)) {
            //TODO: send message
            Bukkit.getOnlinePlayers().forEach { it.showPlayer(Skylife.instance, sender) }
            Utility.vanishedPlayers.remove(sender)
        } else {
            //TODO: send message
            Utility.vanishedPlayers.add(sender)
            Bukkit.getOnlinePlayers().forEach { it.hidePlayer(Skylife.instance, sender) }

        }


        return false
    }
}