package com.carinaschoppe.skylife.testutil

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class TestCommand(name: String) : Command(name) {
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return true
    }
}
