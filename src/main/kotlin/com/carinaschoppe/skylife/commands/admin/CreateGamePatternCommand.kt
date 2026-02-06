package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Handles administrative commands for managing game patterns.
 * This includes creating, saving, and deleting game patterns, which serve as templates for game instances.
 *
 * Command Usage:
 * - `/game create <name>` - Creates a new game pattern.
 * - `/game save <name>` - Saves a configured game pattern to a file.
 * - `/game delete <name>` - Deletes a game pattern and its file.
 */
class CreateGamePatternCommand : CommandExecutor, TabCompleter {

    /**
     * Executes the game pattern management commands.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("game", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (args.size != 2) {
            sender.sendMessage(Messages.ERROR_ARGUMENT)
            return true
        }

        val action = args[0].lowercase()
        val name = args[1]

        when (action) {
            "create" -> {
                if (!sender.hasPermission("skylife.admin.create")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                // Check if pattern already exists
                if (GameCluster.gamePatterns.any { it.mapName.equals(name, ignoreCase = true) }) {
                    sender.sendMessage(Messages.ERROR_PATTERN)
                    return true
                }

                // Check if player already has an active setup
                if (GameSetupCommand.activeSetups.containsKey(sender)) {
                    sender.sendMessage(Messages.ERROR_ARGUMENT)
                    return true
                }

                // Create the new pattern and start setup session
                val pattern = GamePattern(name)
                GameSetupCommand.activeSetups[sender] = pattern

                sender.sendMessage(Messages.GAME_CREATED(name))
                sender.sendMessage(Messages.PREFIX.append(net.kyori.adventure.text.Component.text("Use /gamesetup to open the setup GUI or use commands like /setlocation, /playeramount", Messages.MESSAGE_COLOR)))
            }

            "save" -> {
                if (!sender.hasPermission("skylife.admin.save")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }

                // Check if it's the player's active setup first
                val game = if (GameSetupCommand.activeSetups[sender]?.mapName.equals(name, ignoreCase = true)) {
                    GameSetupCommand.activeSetups[sender]
                } else {
                    // Otherwise look in already saved patterns
                    GameCluster.gamePatterns.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
                        ?: GameSetupCommand.activeSetups.values.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
                }

                if (game == null) {
                    sender.sendMessage(Messages.PREFIX.append(net.kyori.adventure.text.Component.text("Game pattern '$name' not found!", Messages.ERROR_COLOR)))
                    return true
                }

                if (!game.isComplete()) {
                    sender.sendMessage(Messages.GAME_PATTERN_NOT_FULLY_DONE(game.mapName))
                    return true
                }

                // Add to cluster if not already there
                if (!GameCluster.gamePatterns.contains(game)) {
                    GameCluster.addGamePattern(game)
                }

                // Save to file
                GameLoader.saveGameToFile(game)

                // Remove from active setups
                GameSetupCommand.activeSetups.remove(sender)

                sender.sendMessage(Messages.GAME_SAVED)
            }

            "delete" -> {
                if (!sender.hasPermission("skylife.admin.delete")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }

                // Check in saved patterns and active setups
                val game = GameCluster.gamePatterns.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
                    ?: GameSetupCommand.activeSetups.values.firstOrNull { it.mapName.equals(name, ignoreCase = true) }

                if (game == null) {
                    sender.sendMessage(Messages.PREFIX.append(net.kyori.adventure.text.Component.text("Game pattern '$name' not found!", Messages.ERROR_COLOR)))
                    return true
                }

                // Remove from cluster if it's there
                GameCluster.removeGamePattern(game)

                // Remove from active setups if it's there
                GameSetupCommand.activeSetups.entries.removeIf { it.value == game }

                // Delete file if exists
                GameLoader.deleteGameFile(game)

                sender.sendMessage(Messages.GAME_DELETED)
            }

            else -> {
                sender.sendMessage(Messages.ERROR_ARGUMENT)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> listOf("create", "save", "delete")
                .filter { it.startsWith(args[0].lowercase()) }

            2 -> when (args[0].lowercase()) {
                "save", "delete" -> GameCluster.gamePatterns
                    .map { it.mapName }
                    .filter { it.lowercase().startsWith(args[1].lowercase()) }

                else -> emptyList()
            }

            else -> emptyList()
        }
    }
}
