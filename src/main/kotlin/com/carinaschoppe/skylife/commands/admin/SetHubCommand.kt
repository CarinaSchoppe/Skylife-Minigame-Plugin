package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.hub.HubManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command to set the hub spawn location.
 * Usage: /sethub
 * Sets the hub spawn to the player's current location.
 */
class SetHubCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.admin.sethub")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        // Set hub spawn to player's current location
        HubManager.setHubSpawn(sender.location)

        sender.sendMessage(
            Messages.PREFIX.append(
                Component.text("Hub spawn set to your current location!", Messages.MESSAGE_COLOR, TextDecoration.BOLD)
            )
        )

        return true
    }
}
