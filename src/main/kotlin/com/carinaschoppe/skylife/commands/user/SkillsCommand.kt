package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GUIs
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SkillsCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.label != "skills") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.instance.ERROR_NOTPLAYER)
            return false
        }
        if (!sender.hasPermission("skylife.skills")) {
            sender.sendMessage(Messages.instance.ERROR_PERMISSION)
            return false
        }

        sender.openInventory(GUIs.SKILL_SELECT_INVENTORY())

        return false
    }


}