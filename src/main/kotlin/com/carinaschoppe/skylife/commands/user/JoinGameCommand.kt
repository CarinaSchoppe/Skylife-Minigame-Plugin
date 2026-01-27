package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Handles the command for a player to join a game.
 * Players can join a specific game by map name or a random available game.
 *
 * Command Usage:
 * - `/join` - Joins a random game.
 * - `/join random` - Joins a random game.
 * - `/join <mapName>` - Joins a game with the specified map name.
 */
class JoinGameCommand : CommandExecutor, TabCompleter {

    /**
     * Executes the join game command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("join", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (GameCluster.getGame(sender) != null) {
            sender.sendMessage(Messages.ALLREADY_IN_GAME)
            return true
        }

        if (!sender.hasPermission("skylife.join")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        if (args.size > 1) {
            sender.sendMessage(Messages.ERROR_ARGUMENT)
            return true
        }

        // Determine if joining a random game or a specific one
        val mapToJoin = args.firstOrNull()

        if (mapToJoin == null || mapToJoin.equals("random", ignoreCase = true)) {
            // Join a random game
            if (!sender.hasPermission("skylife.join.random")) {
                sender.sendMessage(Messages.ERROR_PERMISSION)
                return true
            }
            if (!GameCluster.addPlayerToRandomGame(sender)) {
                sender.sendMessage(Messages.ERROR_NO_GAME)
            }
        } else {
            // Join a specific game by map name
            if (!sender.hasPermission("skylife.join.map")) {
                sender.sendMessage(Messages.ERROR_PERMISSION)
                return true
            }
            if (!GameCluster.addPlayerToGame(sender, mapToJoin)) {
                if (GameCluster.getGameByName(mapToJoin) == null) {
                    sender.sendMessage(Messages.GAME_NOT_EXISTS(mapToJoin))
                } else {
                    sender.sendMessage(Messages.ERROR_GAME_FULL_OR_STARTED())
                }
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val mapNames = GameCluster.gamePatterns.map { it.mapName }
            return (listOf("random") + mapNames)
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
}
