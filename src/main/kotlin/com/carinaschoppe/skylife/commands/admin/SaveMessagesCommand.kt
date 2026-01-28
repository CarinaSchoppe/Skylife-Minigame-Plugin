package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.utility.messages.MessageLoader
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command to export current messages to messages.json for manual editing.
 * Usage: /savemessages
 *
 * Note: The messages.json file is for reference only. Due to technical limitations
 * with Kotlin objects, changes to messages.json will NOT be loaded automatically.
 * This command allows you to export the current message templates for documentation
 * or reference purposes.
 */
class SaveMessagesCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("skylife.admin.savemessages")) {
                sender.sendMessage(Messages.ERROR_PERMISSION)
                return true
            }
        }

        MessageLoader.saveMessages()

        sender.sendMessage(
            Messages.PREFIX.append(
                Component.text("Messages exported to messages.json!", Messages.MESSAGE_COLOR, TextDecoration.BOLD)
            )
        )

        sender.sendMessage(
            Component.text("Note: The messages.json file is for reference only.", Messages.MESSAGE_COLOR)
        )

        return true
    }
}
