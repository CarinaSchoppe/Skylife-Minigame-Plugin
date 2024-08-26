package com.carinaschoppe.skylife

import com.carinaschoppe.skylife.commands.admin.CreateGamePatternCommand
import com.carinaschoppe.skylife.commands.admin.PlayerAmountCommand
import com.carinaschoppe.skylife.commands.admin.SetLocationCommand
import com.carinaschoppe.skylife.commands.user.*
import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.events.player.*
import com.carinaschoppe.skylife.game.miscellaneous.GameLoader
import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.configuration.Configurations
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

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
        val folderLocation = "/Skylife/"
    }


    override fun onEnable() {
        // Plugin startup logic
        instance = this
        Configurations.instance = Configurations()
        Messages.instance = Messages()
        initialize(Bukkit.getPluginManager())
        Bukkit.getServer().consoleSender.sendMessage(Messages.instance.PREFIX.append(Component.text("Skylife has been started!", Messages.instance.MESSAGE_COLOR)))
    }

    private fun initialize(pluginManager: PluginManager) {
        ConfigurationLoader.saveConfiguration()
        ConfigurationLoader.loadConfiguration()

        DatabaseConnector.connectDatabase()
        StatsUtility.loadAllPlayersIntoStatsPlayer()
        GameLoader.findAllGames().forEach { GameLoader.loadGameFromFile(it) }
        getCommand("join")?.setExecutor(JoinCommand())
        getCommand("start")?.setExecutor(StartCommand())
        getCommand("game")?.setExecutor(CreateGamePatternCommand())
        getCommand("setlocation")?.setExecutor(SetLocationCommand())
        getCommand("playeramount")?.setExecutor(PlayerAmountCommand())
        getCommand("leave")?.setExecutor(LeaveCommand())
        getCommand("stats")?.setExecutor(StatsCommand())
        getCommand("overview")?.setExecutor(GameOverviewCommand())

        pluginManager.registerEvents(PlayerJoinsServerEvent(), this)
        pluginManager.registerEvents(PlayerLoosesSaturationEvent(), this)
        pluginManager.registerEvents(PlayerDisconnectsServerEvent(), this)
        pluginManager.registerEvents(PlayerDeathEvent(), this)
        pluginManager.registerEvents(PlayerChatsEvent(), this)
        pluginManager.registerEvents(PlayerPlacesBlockEvent(), this)
        pluginManager.registerEvents(PlayerBreaksBlockEvent(), this)
        pluginManager.registerEvents(PlayerMovesIntoGameEvent(), this)
        pluginManager.registerEvents(PlayerDamagesEvent(), this)
        pluginManager.registerEvents(PlayerSelectGameEvent(), this)


        //Create game_maps folder if
        val folder = File(Bukkit.getServer().worldContainer, "game_maps")

        if (!folder.exists()) {
            folder.mkdir()
        }

    }


    override fun onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().consoleSender.sendMessage(Messages.instance.PREFIX.append(Component.text("Skylife has been stopped!", Messages.instance.ERROR_COLOR)))
    }
}
