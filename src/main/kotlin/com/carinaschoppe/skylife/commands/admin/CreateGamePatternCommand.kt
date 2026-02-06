package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
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
            "create" -> handleCreate(sender, name)
            "save" -> handleSave(sender, name)
            "delete" -> handleDelete(sender, name)

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

    private fun handleCreate(player: Player, name: String) {
        if (!player.hasPermission("skylife.admin.create")) {
            player.sendMessage(Messages.ERROR_PERMISSION)
            return
        }
        if (GameCluster.gamePatterns.any { it.mapName.equals(name, ignoreCase = true) }) {
            player.sendMessage(Messages.ERROR_PATTERN)
            return
        }
        if (GameSetupCommand.activeSetups.containsKey(player)) {
            player.sendMessage(Messages.ERROR_ARGUMENT)
            return
        }

        val pattern = GamePattern(name)
        GameSetupCommand.activeSetups[player] = pattern

        player.sendMessage(Messages.GAME_CREATED(name))
        player.sendMessage(
            Messages.PREFIX.append(
                Component.text(
                    "Use /gamesetup to open the setup GUI or use commands like /setlocation, /playeramount",
                    Messages.MESSAGE_COLOR
                )
            )
        )
    }

    private fun handleSave(player: Player, name: String) {
        if (!player.hasPermission("skylife.admin.save")) {
            player.sendMessage(Messages.ERROR_PERMISSION)
            return
        }

        val game = findPatternForSave(player, name)
        if (game == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("Game pattern '$name' not found!", Messages.ERROR_COLOR)))
            return
        }

        if (!game.isComplete()) {
            player.sendMessage(Messages.GAME_PATTERN_NOT_FULLY_DONE(game.mapName))
            return
        }

        if (!GameCluster.gamePatterns.contains(game)) {
            GameCluster.addGamePattern(game)
        }

        GameLoader.saveGameToFile(game)
        GameSetupCommand.activeSetups.remove(player)

        player.sendMessage(Messages.GAME_SAVED)
    }

    private fun handleDelete(player: Player, name: String) {
        if (!player.hasPermission("skylife.admin.delete")) {
            player.sendMessage(Messages.ERROR_PERMISSION)
            return
        }

        val game = GameCluster.gamePatterns.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
            ?: GameSetupCommand.activeSetups.values.firstOrNull { it.mapName.equals(name, ignoreCase = true) }

        if (game == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("Game pattern '$name' not found!", Messages.ERROR_COLOR)))
            return
        }

        GameCluster.removeGamePattern(game)
        GameSetupCommand.activeSetups.entries.removeIf { it.value == game }
        GameLoader.deleteGameFile(game)

        player.sendMessage(Messages.GAME_DELETED)
    }

    private fun findPatternForSave(player: Player, name: String): GamePattern? {
        val activePattern = GameSetupCommand.activeSetups[player]
        if (activePattern?.mapName.equals(name, ignoreCase = true)) {
            return activePattern
        }

        return GameCluster.gamePatterns.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
            ?: GameSetupCommand.activeSetups.values.firstOrNull { it.mapName.equals(name, ignoreCase = true) }
    }
}
