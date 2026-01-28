package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
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

        // Support both modes: with mapName or without (using active setup)
        val game: GamePattern?
        val type: String
        val amount: Int?

        when (args.size) {
            2 -> {
                // Use active setup: /playeramount <min|max> <amount>
                game = GameSetupCommand.activeSetups[sender]
                if (game == null) {
                    sender.sendMessage(Messages.PREFIX.append(net.kyori.adventure.text.Component.text("No active setup! Use /game create <name> or /gamesetup <name> first.", Messages.ERROR_COLOR)))
                    return true
                }
                type = args[0].lowercase()
                amount = args[1].toIntOrNull()
            }

            3 -> {
                // Traditional mode: /playeramount <mapName> <min|max> <amount>
                val mapName = args[0]
                type = args[1].lowercase()
                amount = args[2].toIntOrNull()

                game = GameCluster.gamePatterns.firstOrNull { it.mapName.equals(mapName, ignoreCase = true) }
                    ?: GameSetupCommand.activeSetups.values.firstOrNull { it.mapName.equals(mapName, ignoreCase = true) }

                if (game == null) {
                    sender.sendMessage(Messages.GAME_DELETED)
                    return true
                }
            }

            else -> {
                sender.sendMessage(Messages.ERROR_ARGUMENT)
                return true
            }
        }

        if (amount == null || amount < 1) {
            sender.sendMessage(Messages.PREFIX.append(net.kyori.adventure.text.Component.text("Amount must be at least 1!", Messages.ERROR_COLOR)))
            return true
        }

        when (type) {
            "min" -> {
                if (!sender.hasPermission("skylife.playeramount.min")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                game.minPlayers = amount
                if (game.maxPlayers < game.minPlayers) {
                    game.maxPlayers = game.minPlayers
                }
                sender.sendMessage(Messages.PLAYER_AMOUNT_SET)
            }

            "max" -> {
                if (!sender.hasPermission("skylife.playeramount.max")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                if (amount < game.minPlayers) {
                    sender.sendMessage(Messages.PREFIX.append(net.kyori.adventure.text.Component.text("Max players must be >= min players (${game.minPlayers})!", Messages.ERROR_COLOR)))
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
            1 -> {
                // Suggest both map names and min/max for shorthand mode
                val maps = GameCluster.gamePatterns.map { it.mapName }
                val options = listOf("min", "max")
                (maps + options).filter { it.lowercase().startsWith(args[0].lowercase()) }
            }

            2 -> {
                // Could be either min/max (if arg 1 is a map) or amount (if arg 1 is min/max)
                if (args[0].lowercase() in listOf("min", "max")) {
                    // Shorthand mode - suggest numbers
                    listOf("1", "2", "4", "8", "16")
                } else {
                    // Traditional mode - suggest min/max
                    listOf("min", "max").filter { it.startsWith(args[1].lowercase()) }
                }
            }

            else -> emptyList()
        }
    }
}