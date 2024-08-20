package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.management.GameCluster
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateGamePatternCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (command.label != "creategame") return false

        if (args == null) {
            return false
            //TODO: send message
        }

        if (sender !is Player) {
            return false
            //TODO: send message
        }

        val name = args[0]
        if (GameCluster.gamePatterns.any { it.mapName == name }) {
            //todo: send message
            return false
        }


        return false


    }


}