package com.carinaschoppe.skylife.commands.economy

import com.carinaschoppe.skylife.economy.CoinManager
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

/**
 * Command to remove coins from a player.
 * Usage: /removecoins <player> <amount>
 * Permission: skylife.admin.removecoins
 */
class RemoveCoinsCommand : CommandExecutor, TabCompleter {

    private companion object {
        const val PERMISSION_REMOVE_COINS = "skylife.admin.removecoins"
    }

    /**
     * Executes the removecoins command.
     * If the player has insufficient coins, all remaining coins are removed.
     *
     * @param sender The command sender (must have skylife.admin.removecoins permission)
     * @param command The command being executed
     * @param label The alias used for this command
     * @param args Command arguments: [player] [amount]
     * @return true if command was handled, false otherwise
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check permission
        if (!sender.hasPermission(PERMISSION_REMOVE_COINS)) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        // Check arguments
        if (args.size != 2) {
            sender.sendMessage(Messages.COINS_REMOVE_USAGE)
            return true
        }

        // Get target player
        val targetName = args[0]
        val target = Bukkit.getPlayerExact(targetName)
        if (target == null) {
            sender.sendMessage(Messages.PLAYER_NOT_ONLINE(targetName))
            return true
        }

        // Parse amount
        val amount = args[1].toIntOrNull()
        if (amount == null || amount <= 0) {
            sender.sendMessage(Messages.COINS_INVALID_AMOUNT)
            return true
        }

        // Get current balance
        val currentBalance = CoinManager.getCoins(target.uniqueId)

        // Remove coins (will not go below 0)
        val success = CoinManager.removeCoins(target.uniqueId, amount)
        val newBalance = CoinManager.getCoins(target.uniqueId)
        val actualRemoved = currentBalance - newBalance

        if (success) {
            // Notify admin
            sender.sendMessage(Messages.COINS_REMOVED(target.name, actualRemoved, newBalance))

            // Notify player
            target.sendMessage(Messages.COINS_DEDUCTED(actualRemoved))
        } else {
            // Insufficient funds - remove all coins
            sender.sendMessage(Messages.COINS_INSUFFICIENT(target.name, currentBalance, amount))
        }

        return true
    }

    /**
     * Provides tab completion suggestions for the removecoins command.
     *
     * @param sender The command sender
     * @param command The command being executed
     * @param alias The alias used for this command
     * @param args Current command arguments
     * @return List of suggestions based on current input
     */
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (!sender.hasPermission(PERMISSION_REMOVE_COINS)) {
            return emptyList()
        }

        return when (args.size) {
            1 -> {
                // Suggest online player names
                Bukkit.getOnlinePlayers()
                    .map { it.name }
                    .filter { it.startsWith(args[0], ignoreCase = true) }
            }

            2 -> {
                // Suggest common amounts
                listOf("10", "50", "100", "500", "1000")
                    .filter { it.startsWith(args[1]) }
            }

            else -> emptyList()
        }
    }
}
