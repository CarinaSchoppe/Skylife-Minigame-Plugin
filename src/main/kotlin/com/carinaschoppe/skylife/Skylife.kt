package com.carinaschoppe.skylife

import com.carinaschoppe.skylife.commands.admin.CreateGamePatternCommand
import com.carinaschoppe.skylife.commands.admin.PlayerAmountCommand
import com.carinaschoppe.skylife.commands.admin.SetIngameLocationCommand
import com.carinaschoppe.skylife.commands.user.*
import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.events.kit.KitSelectorListener
import com.carinaschoppe.skylife.events.player.*
import com.carinaschoppe.skylife.events.skills.SkillFeatherfallListener
import com.carinaschoppe.skylife.events.skills.SkillInvisibleStalkerListener
import com.carinaschoppe.skylife.events.skills.SkillJumboListener
import com.carinaschoppe.skylife.events.skills.SkillLuckyBirdListener
import com.carinaschoppe.skylife.events.ui.SkillsGuiListener
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.messages.MessageLoader
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

open class Skylife : JavaPlugin() {


    //TODO: events
    //TODO: party (maybe)
    //TODO: scoreboard
    //TODO: statistics (kill, death) (done maybe)
    //TODO: Skills
    //TODO: Items
    //TODO: testing (later)
    //TODO: GUIs
    //TODO: Config files with postgreSQL


    companion object {
        lateinit var instance: Skylife
        val folderLocation = "Skylife/"
    }


    override fun onEnable() {
        // Plugin startup logic
        instance = this

        // Load custom messages
        MessageLoader.loadMessages()

        initialize(Bukkit.getPluginManager())

        // Load all player stats into cache after database is connected
        StatsUtility.loadAllPlayersIntoStatsPlayer()

        // Load guilds into cache
        com.carinaschoppe.skylife.guild.GuildManager.loadGuilds()

        // Load skills into cache
        com.carinaschoppe.skylife.skills.SkillsManager.loadSkills()

        // Start passive skills task
        com.carinaschoppe.skylife.skills.SkillPassiveItemsTask.start(this)

        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Skylife has been started!", Messages.MESSAGE_COLOR)))
    }

    private fun initialize(pluginManager: PluginManager) {
        ConfigurationLoader.loadConfiguration()
        DatabaseConnector.connectDatabase()
        com.carinaschoppe.skylife.hub.HubManager.loadHubSpawn()
        KitManager.initializeKits()
        GameLoader.findAllGames().forEach { GameLoader.loadGameFromFile(it) }
        getCommand("join")?.setExecutor(JoinGameCommand())
        getCommand("start")?.setExecutor(QuickstartGameCommand())
        getCommand("game")?.setExecutor(CreateGamePatternCommand())
        getCommand("setlocation")?.setExecutor(SetIngameLocationCommand())
        getCommand("playeramount")?.setExecutor(PlayerAmountCommand())
        getCommand("sethub")?.setExecutor(com.carinaschoppe.skylife.commands.admin.SetHubCommand())
        getCommand("leave")?.setExecutor(LeaveGameCommand())
        getCommand("stats")?.setExecutor(PlayersStatsCommand())
        getCommand("overview")?.setExecutor(GameOverviewCommand())
        getCommand("skills")?.setExecutor(SkillsListCommand())
        val guildCommand = GuildCommand()
        getCommand("guild")?.setExecutor(guildCommand)
        getCommand("guild")?.tabCompleter = guildCommand
        val messageCommand = MessageCommand()
        getCommand("msg")?.setExecutor(messageCommand)
        getCommand("msg")?.tabCompleter = messageCommand

        pluginManager.registerEvents(PlayerLoosesSaturationListener(), this)
        pluginManager.registerEvents(PlayerDisconnectsServerListener(), this)
        pluginManager.registerEvents(PlayerChatsListener(), this)
        pluginManager.registerEvents(PlayerPlacesBlockListener(), this)
        pluginManager.registerEvents(PlayerBreaksBlockListener(), this)
        pluginManager.registerEvents(PlayerMovesIntoGameListener(), this)
        pluginManager.registerEvents(PlayerDamagesListener(), this)
        pluginManager.registerEvents(PlayerDeathListener(), this)
        pluginManager.registerEvents(PlayerJoinsServerListener(), this)
        pluginManager.registerEvents(PlayerEntersPortalListener(), this)
        pluginManager.registerEvents(PlayerGameOverviewItemListener(), this)
        pluginManager.registerEvents(KitSelectorListener(), this)
        pluginManager.registerEvents(PlayerSelectGameListener(), this)
        pluginManager.registerEvents(PlayerDisplayNameListener(), this)

        addSkillListeners(pluginManager)

        //Create game_maps folder if (MockBukkit doesn't implement worldContainer)
        val worldContainer = runCatching { Bukkit.getServer().worldContainer }
            .getOrElse { Bukkit.getServer().pluginsFolder }
        val folder = File(worldContainer, "game_maps")

        if (!folder.exists()) {
            folder.mkdir()
        }

    }

    private fun addSkillListeners(pluginManager: PluginManager) {
        pluginManager.registerEvents(SkillsGuiListener(), this)
        pluginManager.registerEvents(PlayerSkillsItemListener(), this)
        pluginManager.registerEvents(SkillJumboListener(), this)
        pluginManager.registerEvents(SkillFeatherfallListener(), this)
        pluginManager.registerEvents(SkillInvisibleStalkerListener(), this)
        pluginManager.registerEvents(SkillLuckyBirdListener(), this)
    }


    override fun onDisable() {
        // Stop passive skills task
        com.carinaschoppe.skylife.skills.SkillPassiveItemsTask.stop()

        // Plugin shutdown logic
        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Skylife has been stopped!", Messages.ERROR_COLOR)))
    }
}
