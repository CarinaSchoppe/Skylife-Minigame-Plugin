package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.chat.ChatManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Command handler for direct messages between players.
 * Usage: /msg <player> <message>
 */
class MessageCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.msg")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        if (args.size < 2) {
            sender.sendMessage(Messages.PREFIX.append(Component.text("Usage: /msg <player> <message>", Messages.ERROR_COLOR)))
            return true
        }

        val targetName = args[0]
        val target = Bukkit.getPlayerExact(targetName)

        if (target == null) {
            sender.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND)
            return true
        }

        if (target == sender) {
            sender.sendMessage(Messages.PREFIX.append(Component.text("You cannot send a message to yourself", Messages.ERROR_COLOR)))
            return true
        }

        // Check if direct message is allowed based on chat rules
        if (!ChatManager.canSendDirectMessage(sender, target)) {
            sender.sendMessage(Messages.PREFIX.append(Component.text("You cannot send a direct message to this player right now", Messages.ERROR_COLOR)))
            return true
        }

        val message = args.slice(1 until args.size).joinToString(" ")

        // Format: [DM] Sender -> You: message
        val toTarget = Component.text("[DM] ", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
            .append(sender.displayName())
            .append(Component.text(" -> ", NamedTextColor.GRAY))
            .append(Component.text("You", Messages.ACCENT_COLOR))
            .append(Component.text(": ", NamedTextColor.WHITE))
            .append(Component.text(message, NamedTextColor.WHITE))

        // Format: [DM] You -> Target: message
        val toSender = Component.text("[DM] ", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
            .append(Component.text("You", Messages.ACCENT_COLOR))
            .append(Component.text(" -> ", NamedTextColor.GRAY))
            .append(target.displayName())
            .append(Component.text(": ", NamedTextColor.WHITE))
            .append(Component.text(message, NamedTextColor.WHITE))

        target.sendMessage(toTarget)
        sender.sendMessage(toSender)

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers()
                .filter { it != sender }
                .map { it.name }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
}
