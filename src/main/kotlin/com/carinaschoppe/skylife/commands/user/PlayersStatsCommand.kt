package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Handles the command to display player statistics.
 * Players can view their own stats or, with permission, the stats of others.
 *
 * Command Usage:
 * - `/stats` - Displays the sender's own statistics.
 * - `/stats <playerName>` - Displays the statistics for the specified player.
 */
class PlayersStatsCommand : CommandExecutor, TabCompleter {

    /**
     * Executes the statistics command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("stats", ignoreCase = true)) return false

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.stats")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        val targetName = if (args.isEmpty()) sender.name else args[0]

        // Check for permission if viewing other players' stats
        if (args.isNotEmpty() && !sender.hasPermission("skylife.stats.other")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        try {
            // Get the stats player from database
            val (statsPlayer, rank) = transaction {
                // Get all players sorted by points for ranking
                val allStats = StatsPlayer.all().sortedByDescending { it.points }
                val player = allStats.firstOrNull { it.name.equals(targetName, ignoreCase = true) }

                if (player == null) {
                    sender.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND())
                    return@transaction null
                }

                // Calculate rank (1-based index)
                val playerRank = allStats.indexOfFirst { it.uuid == player.uuid } + 1
                player to playerRank
            } ?: return true // Exit if player not found

            // Send the stats message with the correct signature
            val isViewingOwnStats = sender.name.equals(targetName, ignoreCase = true)
            sender.sendMessage(
                Messages.STATS(
                    own = isViewingOwnStats,
                    name = statsPlayer.name,
                    kills = statsPlayer.kills,
                    deaths = statsPlayer.deaths,
                    wins = statsPlayer.wins,
                    games = statsPlayer.games,
                    points = statsPlayer.points,
                    rank = rank
                )
            )
        } catch (e: Exception) {
            sender.sendMessage(Messages.ERROR_COMMAND())
            e.printStackTrace() // Log the error for debugging
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
}