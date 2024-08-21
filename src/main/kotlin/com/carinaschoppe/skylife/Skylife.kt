package com.carinaschoppe.skylife

import com.carinaschoppe.skylife.commands.admin.CreateGamePatternCommand
import com.carinaschoppe.skylife.commands.admin.PlayerAmountCommand
import com.carinaschoppe.skylife.commands.admin.SetLocationCommand
import com.carinaschoppe.skylife.commands.user.JoinCommand
import com.carinaschoppe.skylife.commands.user.LeaveCommand
import com.carinaschoppe.skylife.commands.user.StartCommand
import com.carinaschoppe.skylife.commands.user.StatsCommand
import com.carinaschoppe.skylife.game.miscellaneous.GameLoader
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

class Skylife : JavaPlugin() {


    //TODO: messages
    //TODO: events
    //TODO: party
    //TODO: scoreboard
    //TODO: statistics (kill, death)
    //TODO: Skills
    //TODO: Items
    //TODO: testing
    //TODO: GUIs
    //Config files


    companion object {
        lateinit var instance: Skylife

    }


    override fun onEnable() {
        // Plugin startup logic
        instance = this
        initialize(Bukkit.getPluginManager())
        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Skylife has been started!", Messages.Me)))
    }

    private fun initialize(pluginManager: PluginManager) {
        GameLoader.findAllGames().forEach { GameLoader.loadGameFromFile(it) }
        getCommand("join")?.setExecutor(JoinCommand())
        getCommand("start")?.setExecutor(StartCommand())
        getCommand("game")?.setExecutor(CreateGamePatternCommand())
        getCommand("setlocation")?.setExecutor(SetLocationCommand())
        getCommand("playeramount")?.setExecutor(PlayerAmountCommand())
        getCommand("leave")?.setExecutor(LeaveCommand())
        getCommand("stats")?.setExecutor(StatsCommand())
    }


    override fun onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().consoleSender.sendMessage("${prefix} Skylife has been stopped!")
    }
}
