package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

/**
 * Command to delete a game pattern and all its active game instances.
 * Usage: /deletegame <name>
 *
 * This command:
 * 1. Removes all active game instances based on the pattern
 * 2. Removes all lobby game instances based on the pattern
 * 3. Removes the game pattern from the cluster
 * 4. Deletes the game pattern file
 *
 * Requires permission: skylife.admin.deletegame
 */
class DeleteGameCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("skylife.admin.deletegame")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(
                Messages.PREFIX.append(
                    Component.text("Usage: /deletegame <name>", Messages.ERROR_COLOR)
                )
            )
            return true
        }

        val gameName = args[0]

        // Find the game pattern
        val gamePattern = GameCluster.gamePatterns.firstOrNull {
            it.mapName.equals(gameName, ignoreCase = true)
        }

        if (gamePattern == null) {
            sender.sendMessage(
                Messages.PREFIX
                    .append(Component.text("Game pattern '", Messages.ERROR_COLOR))
                    .append(Component.text(gameName, Messages.NAME_COLOR))
                    .append(Component.text("' not found!", Messages.ERROR_COLOR))
            )
            return true
        }

        // Count how many games will be removed
        val activeGames = GameCluster.activeGamesList.filter { it.pattern == gamePattern }
        val lobbyGames = GameCluster.lobbyGamesList.filter { it.pattern == gamePattern }
        val totalGames = activeGames.size + lobbyGames.size

        // Stop all active games based on this pattern
        activeGames.forEach { game ->
            GameCluster.stopGame(game)
        }

        // Remove all lobby games based on this pattern
        lobbyGames.forEach { game ->
            // Kick all players from the lobby game
            game.getAllPlayers().forEach { player ->
                GameCluster.removePlayerFromGame(player)
            }
        }

        // Remove the pattern from the cluster
        GameCluster.gamePatterns.remove(gamePattern)

        // Remove from active setups if it's there
        GameSetupCommand.activeSetups.entries.removeIf { it.value == gamePattern }

        // Delete the file
        GameLoader.deleteGameFile(gamePattern)

        sender.sendMessage(
            Messages.PREFIX
                .append(Component.text("Game pattern '", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                .append(Component.text("' and ", Messages.MESSAGE_COLOR))
                .append(Component.text(totalGames.toString(), Messages.NAME_COLOR))
                .append(Component.text(" game instance(s) have been deleted!", Messages.MESSAGE_COLOR))
        )

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            return GameCluster.gamePatterns
                .map { it.mapName }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
}
