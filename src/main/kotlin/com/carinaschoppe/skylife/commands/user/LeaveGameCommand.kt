package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveGameCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.label != "leave") return false
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return false
        }

        if (!(GameCluster.lobbyGames.any { game -> game.livingPlayers.contains(sender) or game.spectators.contains(sender) } or GameCluster.activeGames.any { game -> game.livingPlayers.contains(sender) or game.spectators.contains(sender) })) {
            sender.sendMessage(Messages.NOT_INGAME)
            return false
        }

        if (!sender.hasPermission("skylife.leave")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return false
        }

        GameCluster.removePlayerFromGame(sender)
        sender.sendMessage(Messages.OWN_PLAYER_LEFT)
        StatsUtility.addStatsToPlayerWhenLeave(sender)

        return false
    }


}