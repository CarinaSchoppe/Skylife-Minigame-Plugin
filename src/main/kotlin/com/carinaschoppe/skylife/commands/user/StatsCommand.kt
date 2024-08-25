package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsPlayer
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

class StatsCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "vanish") return false

        if (sender !is Player) {
            sender.sendMessage(Messages.instance.ERROR_NOTPLAYER)
            return false
        }

        if (!sender.hasPermission("skylife.stats")) {
            sender.sendMessage(Messages.instance.ERROR_PERMISSION)
            return false
        }

        if (args == null) {
            //show own stats
            transaction {
                val statsPlayer = StatsUtility.statsPlayers.first { it.uuid == sender.player?.uniqueId.toString() }
                sender.sendMessage(Messages.instance.STATS(true, statsPlayer.kills, statsPlayer.deaths, statsPlayer.wins, statsPlayer.games, statsPlayer.name))
            }


        } else if (args.size != 1) {

            sender.sendMessage(Messages.instance.ERROR_ARGUMENT)
            return false
        }
        val player = args?.get(0)
        var statsPlayer: StatsPlayer? = null
        transaction {
            try {
                statsPlayer = StatsUtility.statsPlayers.firstOrNull { it.name == player }
            } catch (e: Exception) {
                sender.sendMessage(Messages.instance.ERROR_PLAYER_NOT_FOUND())
                return@transaction
            }
        }
        if (statsPlayer == null)
            return false
        sender.sendMessage(Messages.instance.STATS(false, statsPlayer!!.kills, statsPlayer!!.deaths, statsPlayer!!.wins, statsPlayer!!.games, statsPlayer!!.name))

        return false
    }
}