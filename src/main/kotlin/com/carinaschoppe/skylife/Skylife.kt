package com.carinaschoppe.skylife

import com.carinaschoppe.skylife.commands.admin.CreateGamePatternCommand
import com.carinaschoppe.skylife.commands.admin.PlayerAmountCommand
import com.carinaschoppe.skylife.commands.admin.SetLocationCommand
import com.carinaschoppe.skylife.commands.admin.VanishCommand
import com.carinaschoppe.skylife.commands.user.JoinCommand
import com.carinaschoppe.skylife.commands.user.LeaveCommand
import com.carinaschoppe.skylife.commands.user.StartCommand
import com.carinaschoppe.skylife.game.miscellaneous.GameLoader
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

class Skylife : JavaPlugin() {


    //TODO: messages
    //TODO: events
    //TODO: party
    //TODO: scoreboard
    //TODO: statistics
    //TODO: Skills
    //TODO: Items
    //TODO:


    companion object {
        lateinit var instance: Skylife
        var prefix: String = "§8[§6SkyLife§8] §7"
    }


    override fun onEnable() {
        // Plugin startup logic
        instance = this


        initialize(Bukkit.getPluginManager())

        Bukkit.getServer().consoleSender.sendMessage("${prefix} Skylife has been started!")
    }

    private fun initialize(pluginManager: PluginManager) {
        GameLoader.findAllGames().forEach { GameLoader.loadGameFromFile(it) }
        getCommand("join")?.setExecutor(JoinCommand())
        getCommand("start")?.setExecutor(StartCommand())
        getCommand("game")?.setExecutor(CreateGamePatternCommand())
        getCommand("setlocation")?.setExecutor(SetLocationCommand())
        getCommand("playeramount")?.setExecutor(PlayerAmountCommand())
        getCommand("vanish")?.setExecutor(VanishCommand())
        getCommand("leave")?.setExecutor(LeaveCommand())

    }


    override fun onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().consoleSender.sendMessage("${prefix} Skylife has been stopped!")
    }
}
