package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GameManagementGui
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command to open the game management GUI for admins and devs.
 * Usage: /managegames
 *
 * This command opens a GUI where admins can:
 * - View all game patterns
 * - Edit min/max players
 * - Edit minPlayersToStart
 * - Delete games
 *
 * Requires permission: skylife.admin.managegames
 */
class ManageGamesCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.admin.managegames")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        if (GameCluster.gamePatterns.isEmpty()) {
            sender.sendMessage(Messages.ERROR_NO_PATTERN)
            return true
        }

        // Open the game management GUI
        GameManagementGui.openListGUI(sender)
        return true
    }
}
