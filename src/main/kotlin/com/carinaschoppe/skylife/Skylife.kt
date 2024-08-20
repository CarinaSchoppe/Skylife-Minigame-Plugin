package com.carinaschoppe.skylife

import com.carinaschoppe.skylife.commands.user.JoinCommand
import com.carinaschoppe.skylife.game.miscellaneous.GameLoader
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

class Skylife : JavaPlugin() {


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

    }


    override fun onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().consoleSender.sendMessage("${prefix} Skylife has been stopped!")
    }
}
