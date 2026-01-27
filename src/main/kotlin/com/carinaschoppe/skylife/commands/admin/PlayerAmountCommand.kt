package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Handles the command to set the minimum and maximum player amounts for a game pattern.
 *
 * Command Usage:
 * - `/playeramount <mapName> <min|max> <amount>`
 */
class PlayerAmountCommand : CommandExecutor, TabCompleter {

    /**
     * Executes the player amount setting command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("playeramount", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (args.size != 3) {
            sender.sendMessage(Messages.ERROR_ARGUMENT)
            return true
        }

        val mapName = args[0]
        val type = args[1].lowercase()
        val amount = args[2].toIntOrNull()

        if (amount == null || amount < 0) {
            sender.sendMessage(Messages.ERROR_ARGUMENT) // Or a more specific "invalid number" message
            return true
        }

        val game = GameCluster.gamePatterns.firstOrNull { it.mapName.equals(mapName, ignoreCase = true) }
        if (game == null) {
            sender.sendMessage(Messages.GAME_DELETED) // Or GAME_NOT_FOUND
            return true
        }

        when (type) {
            "min" -> {
                if (!sender.hasPermission("skylife.playeramount.min")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                game.minPlayers = amount
                sender.sendMessage(Messages.PLAYER_AMOUNT_SET)
            }

            "max" -> {
                if (!sender.hasPermission("skylife.playeramount.max")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                game.maxPlayers = amount
                sender.sendMessage(Messages.PLAYER_AMOUNT_SET)
            }

            else -> {
                sender.sendMessage(Messages.ERROR_ARGUMENT)
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> GameCluster.gamePatterns
                .map { it.mapName }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }

            2 -> listOf("min", "max")
                .filter { it.startsWith(args[1].lowercase()) }

            else -> emptyList()
        }
    }
}