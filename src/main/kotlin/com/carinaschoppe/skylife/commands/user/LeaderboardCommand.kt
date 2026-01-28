package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Handles the leaderboard command to display top players based on various statistics.
 *
 * Command Usage:
 * - `/leaderboard` or `/lb` - Shows top 10 by points (default)
 * - `/leaderboard points` or `/lb points` - Shows top 10 by points
 * - `/leaderboard kills` or `/lb kills` - Shows top 10 by kills
 * - `/leaderboard wins` or `/lb wins` - Shows top 10 by wins
 * - `/leaderboard games` or `/lb games` - Shows top 10 by games played
 * - `/leaderboard kd` or `/lb kd` - Shows top 10 by K/D ratio
 */
class LeaderboardCommand : CommandExecutor, TabCompleter {

    private val validStats = listOf("points", "kills", "wins", "games", "kd")

    /**
     * Executes the leaderboard command.
     *
     * @param sender The entity who sent the command.
     * @param command The command that was executed.
     * @param label The alias of the command used.
     * @param args The arguments provided with the command.
     * @return `true` if the command was handled successfully, `false` otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.label.equals("leaderboard", ignoreCase = true) && !command.label.equals("lb", ignoreCase = true)) {
            return false
        }

        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!sender.hasPermission("skylife.leaderboard")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        // Default to points if no argument provided
        val statType = if (args.isEmpty()) "points" else args[0].lowercase()

        // Validate stat type
        if (!validStats.contains(statType)) {
            sender.sendMessage(Messages.LEADERBOARD_INVALID_STAT)
            return true
        }

        try {
            // Fetch top 10 players from database
            val topPlayers = transaction {
                val allPlayers = StatsPlayer.all().toList()

                // Sort based on stat type
                val sorted = when (statType) {
                    "points" -> allPlayers.sortedByDescending { it.points }
                    "kills" -> allPlayers.sortedByDescending { it.kills }
                    "wins" -> allPlayers.sortedByDescending { it.wins }
                    "games" -> allPlayers.sortedByDescending { it.games }
                    "kd" -> allPlayers.sortedByDescending {
                        if (it.deaths == 0) it.kills.toDouble()
                        else it.kills.toDouble() / it.deaths.toDouble()
                    }

                    else -> allPlayers.sortedByDescending { it.points }
                }

                // Take top 10 and map to display data
                sorted.take(10).mapIndexed { index, player ->
                    LeaderboardEntry(
                        rank = index + 1,
                        name = player.name,
                        value = when (statType) {
                            "points" -> player.points.toString()
                            "kills" -> player.kills.toString()
                            "wins" -> player.wins.toString()
                            "games" -> player.games.toString()
                            "kd" -> {
                                if (player.deaths == 0) player.kills.toString()
                                else String.format("%.2f", player.kills.toDouble() / player.deaths.toDouble())
                            }

                            else -> player.points.toString()
                        }
                    )
                }
            }

            // Send leaderboard to player
            sender.sendMessage(Messages.LEADERBOARD_HEADER(statType))

            if (topPlayers.isEmpty()) {
                sender.sendMessage(Messages.LEADERBOARD_EMPTY)
            } else {
                topPlayers.forEach { entry ->
                    sender.sendMessage(Messages.LEADERBOARD_ENTRY(entry.rank, entry.name, entry.value))
                }
            }

            sender.sendMessage(Messages.LEADERBOARD_FOOTER)

        } catch (e: Exception) {
            sender.sendMessage(Messages.ERROR_COMMAND)
            e.printStackTrace()
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return validStats.filter { it.startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }

    /**
     * Data class representing a leaderboard entry.
     */
    private data class LeaderboardEntry(
        val rank: Int,
        val name: String,
        val value: String
    )
}
