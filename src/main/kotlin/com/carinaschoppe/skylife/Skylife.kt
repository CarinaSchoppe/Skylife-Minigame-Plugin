package com.carinaschoppe.skylife

import com.carinaschoppe.skylife.commands.admin.*
import com.carinaschoppe.skylife.commands.user.*
import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.events.kit.KitSelectorListener
import com.carinaschoppe.skylife.events.player.*
import com.carinaschoppe.skylife.events.skills.SkillFeatherfallListener
import com.carinaschoppe.skylife.events.skills.SkillInvisibleStalkerListener
import com.carinaschoppe.skylife.events.skills.SkillJumboListener
import com.carinaschoppe.skylife.events.skills.SkillLuckyBirdListener
import com.carinaschoppe.skylife.events.ui.GameSetupGuiListener
import com.carinaschoppe.skylife.events.ui.SkillsGuiListener
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.guild.GuildManager
import com.carinaschoppe.skylife.hub.HubManager
import com.carinaschoppe.skylife.skills.SkillPassiveItemsTask
import com.carinaschoppe.skylife.skills.SkillsManager
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

    companion object {
        lateinit var instance: Skylife
        val folderLocation = "Skylife/"
        val config get() = ConfigurationLoader.config
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
        GuildManager.loadGuilds()

        // Load skills into cache
        SkillsManager.loadSkills()

        // Load economy data (coins and kit unlocks)
        com.carinaschoppe.skylife.economy.CoinManager.loadCoins()
        com.carinaschoppe.skylife.economy.KitUnlockManager.loadUnlocks()

        // Load skill unlocks into cache
        com.carinaschoppe.skylife.skills.SkillUnlockManager.loadUnlocks()

        // Start passive skills task
        SkillPassiveItemsTask.start(this)

        // Start lobby scoreboard update task
        com.carinaschoppe.skylife.utility.scoreboard.LobbyScoreboardUpdateTask.start(this)

        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Skylife has been started!", Messages.MESSAGE_COLOR)))
    }

    private fun initialize(pluginManager: PluginManager) {
        ConfigurationLoader.loadConfiguration()
        DatabaseConnector.connectDatabase()
        com.carinaschoppe.skylife.utility.location.LocationManager.loadLocations()
        HubManager.loadHubSpawn()

        // Verify hub is set before allowing server to continue
        if (!HubManager.isHubSpawnSet()) {
            Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("WARNING: Hub spawn is not set! Use /sethub to configure it.", Messages.ERROR_COLOR)))
            Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Players will be teleported to world spawn as fallback.", Messages.ERROR_COLOR)))
        }

        KitManager.initializeKits()

        // Cleanup orphaned worlds from previous crashes
        com.carinaschoppe.skylife.game.managers.MapManager.cleanupOrphanedWorlds()

        GameLoader.findAllGames().forEach { GameLoader.loadGameFromFile(it) }
        registerCommands()
        registerEventListeners(pluginManager)
        createGameMapsFolder()
    }

    private fun registerCommands() {
        getCommand("join")?.setExecutor(JoinGameCommand())
        getCommand("start")?.setExecutor(QuickstartGameCommand())
        getCommand("game")?.setExecutor(CreateGamePatternCommand())
        getCommand("setlocation")?.setExecutor(SetIngameLocationCommand())
        getCommand("playeramount")?.setExecutor(PlayerAmountCommand())
        getCommand("sethub")?.setExecutor(SetHubCommand())
        getCommand("leave")?.setExecutor(LeaveGameCommand())
        getCommand("stats")?.setExecutor(PlayersStatsCommand())
        getCommand("overview")?.setExecutor(GameOverviewCommand())
        getCommand("skills")?.setExecutor(SkillsListCommand())
        getCommand("gamesetup")?.setExecutor(GameSetupCommand())
        getCommand("removespawn")?.setExecutor(RemoveSpawnCommand())
        getCommand("savemessages")?.setExecutor(SaveMessagesCommand())

        val guildCommand = GuildCommand()
        getCommand("guild")?.setExecutor(guildCommand)
        getCommand("guild")?.tabCompleter = guildCommand

        val messageCommand = MessageCommand()
        getCommand("msg")?.setExecutor(messageCommand)
        getCommand("msg")?.tabCompleter = messageCommand

        val partyCommand = PartyCommand()
        getCommand("party")?.setExecutor(partyCommand)
        getCommand("party")?.tabCompleter = partyCommand

        val leaderboardCommand = LeaderboardCommand()
        getCommand("leaderboard")?.setExecutor(leaderboardCommand)
        getCommand("leaderboard")?.tabCompleter = leaderboardCommand

        // Economy commands
        val giveCoinsCommand = com.carinaschoppe.skylife.commands.economy.GiveCoinsCommand()
        getCommand("givecoins")?.setExecutor(giveCoinsCommand)
        getCommand("givecoins")?.tabCompleter = giveCoinsCommand

        val removeCoinsCommand = com.carinaschoppe.skylife.commands.economy.RemoveCoinsCommand()
        getCommand("removecoins")?.setExecutor(removeCoinsCommand)
        getCommand("removecoins")?.tabCompleter = removeCoinsCommand
    }

    private fun registerEventListeners(pluginManager: PluginManager) {
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
        pluginManager.registerEvents(InventoryProtectionListener(), this)
        pluginManager.registerEvents(KitSelectorListener(), this)
        pluginManager.registerEvents(PlayerSelectGameListener(), this)
        pluginManager.registerEvents(PlayerDisplayNameListener(), this)
        pluginManager.registerEvents(PlayerDisconnectsPartyListener(), this)
        registerSkillListeners(pluginManager)
    }

    private fun registerSkillListeners(pluginManager: PluginManager) {
        pluginManager.registerEvents(SkillsGuiListener(), this)
        pluginManager.registerEvents(GameSetupGuiListener(), this)
        pluginManager.registerEvents(PlayerSkillsItemListener(), this)
        pluginManager.registerEvents(SkillJumboListener(), this)
        pluginManager.registerEvents(SkillFeatherfallListener(), this)
        pluginManager.registerEvents(SkillInvisibleStalkerListener(), this)
        pluginManager.registerEvents(SkillLuckyBirdListener(), this)
        pluginManager.registerEvents(com.carinaschoppe.skylife.events.ui.KitPurchaseGuiListener(), this)
    }

    private fun createGameMapsFolder() {
        // Create maps folder in plugin directory
        val mapsFolder = File(dataFolder, "maps")
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs()
            Bukkit.getLogger().info("Created maps folder at: ${mapsFolder.absolutePath}")
            Bukkit.getLogger().info("Place your map templates in this folder!")
        }
    }

    override fun onDisable() {
        // Cleanup all game worlds before shutdown
        com.carinaschoppe.skylife.game.managers.MapManager.cleanupAllWorlds()

        SkillPassiveItemsTask.stop()
        com.carinaschoppe.skylife.utility.scoreboard.LobbyScoreboardUpdateTask.stop()
        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Skylife has been stopped!", Messages.ERROR_COLOR)))
    }
}
