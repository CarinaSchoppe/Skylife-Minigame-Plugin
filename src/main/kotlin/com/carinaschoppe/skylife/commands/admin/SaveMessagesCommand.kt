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
 * Command to export current messages to messages.json for editing.
 * Usage: /savemessages
 *
 * This command exports all static message Components to messages.json in MiniMessage format.
 * After editing the file, restart the server or reload the plugin to load the changes.
 * Only static Component messages are saved - dynamic message functions are not included.
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
            Component.text("Restart the server to load any changes you make.", Messages.MESSAGE_COLOR)
        )

        return true
    }
}
