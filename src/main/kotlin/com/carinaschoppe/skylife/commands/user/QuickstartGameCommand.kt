package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.LobbyState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Handles the command to speed up the lobby countdown of the player's current game.
 * This is often referred to as a "quickstart" or "forcestart" command.
 *
 * Command Usage:
 * - `/start`
 */
class QuickstartGameCommand : CommandExecutor {

    /**
     * Executes the quickstart command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("start", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.start")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        val game = GameCluster.getGame(sender)
        if (game == null) {
            sender.sendMessage(Messages.NOT_INGAME)
            return true
        }

        // Ensure the game is in the lobby state and get the lobby state
        val lobbyState = game.currentState as? LobbyState ?: run {
            sender.sendMessage(Messages.ERROR_COMMAND)
            return true
        }

        // Get the countdown from the lobby state
        val countdown = lobbyState.countdown
        if (!countdown.isRunning) {
            sender.sendMessage(Messages.ROUND_SPEED_LOW) // Countdown not running
            return true
        }

        // Set the countdown to 5 seconds if it's currently longer
        if (countdown.seconds > 5) {
            countdown.reduceTo(5)
            // Broadcast the speed-up message to all players in the game
            game.broadcast(Messages.ROUND_SPEED_ALL)
        } else {
            // Countdown is already at 5 seconds or less
            sender.sendMessage(Messages.ROUND_SPEED_LOW)
        }

        return true
    }
}
