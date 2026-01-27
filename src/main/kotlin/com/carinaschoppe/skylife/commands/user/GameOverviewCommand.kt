package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GUIs
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Handles the command to open the game overview GUI.
 * This allows players to see and join available games.
 *
 * Command Usage:
 * - `/overview`
 */
class GameOverviewCommand : CommandExecutor {

    /**
     * Executes the game overview command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("overview", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.overview")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        sender.openInventory(GUIs.levelSelectInventory())

        return true
    }
}
