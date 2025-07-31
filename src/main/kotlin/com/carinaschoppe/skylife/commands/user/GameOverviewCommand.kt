package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GUIs
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameOverviewCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.label != "overview") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return false
        }

        if (!sender.hasPermission("skylife.overview")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return false
        }

        sender.openInventory(GUIs.LEVEL_SELECT_INVENTORY())

        return false
    }
}