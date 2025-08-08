package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
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
class CreateGamePatternCommand : CommandExecutor {

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
                if (!sender.hasPermission("skylife.create")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                // Check if pattern already exists
                if (GameCluster.gamePatterns.any { it.mapName.equals(name, ignoreCase = true) }) {
                    sender.sendMessage(Messages.ERROR_PATTERN)
                    return true
                }

                // Create and add the new pattern
                val pattern = GamePattern(name)
                GameCluster.gamePatterns.add(pattern)

                // Create a game instance from this pattern
                GameCluster.createGameFromPattern(pattern)
                
                sender.sendMessage(Messages.GAME_CREATED(name))
            }

            "save" -> {
                if (!sender.hasPermission("skylife.save")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                val game = GameCluster.gamePatterns.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
                if (game == null) {
                    sender.sendMessage(Messages.GAME_DELETED) // Using a generic message, consider creating GAME_NOT_FOUND
                    return true
                }

                if (!game.gameLocationManager.gamePatternComplete()) {
                    sender.sendMessage(Messages.GAME_PATTERN_NOT_FULLY_DONE(game.mapName))
                    return true
                }

                GameLoader.saveGameToFile(game)
                sender.sendMessage(Messages.GAME_SAVED)
            }

            "delete" -> {
                if (!sender.hasPermission("skylife.delete")) {
                    sender.sendMessage(Messages.ERROR_PERMISSION)
                    return true
                }
                val game = GameCluster.gamePatterns.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
                if (game == null) {
                    sender.sendMessage(Messages.GAME_DELETED) // Using a generic message
                    return true
                }

                GameCluster.gamePatterns.remove(game)
                GameLoader.deleteGameFile(game)
                sender.sendMessage(Messages.GAME_DELETED)
            }

            else -> {
                sender.sendMessage(Messages.ERROR_ARGUMENT)
            }
        }
        return true
    }
}