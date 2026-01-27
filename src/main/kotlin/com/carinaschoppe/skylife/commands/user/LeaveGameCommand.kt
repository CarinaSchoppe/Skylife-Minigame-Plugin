package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.hub.HubManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Handles the command for a player to leave their current game.
 *
 * Command Usage:
 * - `/leave`
 */
class LeaveGameCommand : CommandExecutor {

    /**
     * Executes the leave game command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("leave", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.leave")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        if (GameCluster.getGame(sender) == null) {
            sender.sendMessage(Messages.NOT_INGAME)
            return true
        }

        // The GameCluster handles the core logic of removing a player.
        GameCluster.removePlayerFromGame(sender)

        // Teleport player to hub
        HubManager.teleportToHub(sender)

        // Ideally, stat updates and messaging should also be handled within the GameCluster
        // to keep the command layer clean and ensure consistent behavior.
        sender.sendMessage(Messages.OWN_PLAYER_LEFT)
        StatsUtility.addStatsToPlayerWhenLeave(sender)

        return true
    }
}