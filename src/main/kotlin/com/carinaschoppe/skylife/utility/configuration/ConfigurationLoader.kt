package com.carinaschoppe.skylife.utility.configuration

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.utility.messages.Messages
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import java.io.File

object ConfigurationLoader {

    var config: Config = Config()
        private set

    fun saveConfiguration() {

        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "config.json")

        //check if path exists if not create and than check for file

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        if (!file.exists()) {
            file.createNewFile()
        }

        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()

        val json: String = gson.toJson(config)
        file.writeText(json)
        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Configuration saved!", Messages.MESSAGE_COLOR)))

    }

    fun loadConfiguration() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "config.json")

        if (!file.exists()) {
            config = Config()
            saveConfiguration()
            Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Configuration loaded!", Messages.MESSAGE_COLOR)))
            return
        }

        GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()

        val json: String = file.readText()
        val result = parseConfigJsonWithMigration(json)
        config = result.config
        if (result.migrated) {
            saveConfiguration()
        }

        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Configuration loaded!", Messages.MESSAGE_COLOR)))
    }

    fun parseConfigJson(json: String): Config {
        return parseConfigJsonWithMigration(json).config
    }

    private data class ConfigMigrationResult(val config: Config, val migrated: Boolean)

    private fun parseConfigJsonWithMigration(json: String): ConfigMigrationResult {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val jsonObject = gson.fromJson(json, JsonObject::class.java) ?: JsonObject()
        val defaults = Config()
        val migration = MigrationTracker()

        applyLegacyScoreboardTitle(jsonObject, defaults, migration)
        applyTimerSettings(jsonObject, defaults, gson)
        applyDatabaseSettings(jsonObject, defaults, gson)
        defaults.scoreboard = resolveScoreboardConfig(jsonObject, defaults, migration, gson)

        return ConfigMigrationResult(defaults, migration.migrated)
    }

    private data class MigrationTracker(var migrated: Boolean = false)

    private fun applyLegacyScoreboardTitle(
        jsonObject: JsonObject,
        defaults: Config,
        migration: MigrationTracker
    ) {
        if (jsonObject.has("scoreboard_title")) {
            defaults.scoreboardTitle = migrateLegacy(jsonObject["scoreboard_title"].asString, migration)
        }
    }

    private fun applyTimerSettings(jsonObject: JsonObject, defaults: Config, gson: Gson) {
        if (jsonObject.has("timer_settings")) {
            defaults.timer = gson.fromJson(jsonObject["timer_settings"], Timer::class.java)
        }
    }

    private fun applyDatabaseSettings(jsonObject: JsonObject, defaults: Config, gson: Gson) {
        if (jsonObject.has("database")) {
            defaults.database = gson.fromJson(jsonObject["database"], DatabaseConfig::class.java)
        }
    }

    private fun resolveScoreboardConfig(
        jsonObject: JsonObject,
        defaults: Config,
        migration: MigrationTracker,
        gson: Gson
    ): ScoreboardConfig {
        val scoreboardDefaults = ScoreboardConfig()
        val scoreboardElement = jsonObject["scoreboard"]
        if (scoreboardElement == null || !scoreboardElement.isJsonObject) {
            return scoreboardDefaults.copy(title = defaults.scoreboardTitle)
        }
        val scoreboardObject = scoreboardElement.asJsonObject

        val serverName = readMigratedString(scoreboardObject, "server_name", scoreboardDefaults.serverName, migration)
        val title = readMigratedString(scoreboardObject, "title", "", migration)
        val animateTitle = readBoolean(scoreboardObject, "animate_title", scoreboardDefaults.animateTitle)
        val lines = readLines(scoreboardObject, "lines", scoreboardDefaults.lines, migration, gson)
        val lobbyTitle = readMigratedString(scoreboardObject, "lobby_title", scoreboardDefaults.lobbyTitle, migration)
        val lobbyLines = readLobbyLines(scoreboardObject, scoreboardDefaults, migration, gson)

        val resolvedTitle = if (title.isBlank()) defaults.scoreboardTitle else title
        return ScoreboardConfig(
            serverName = serverName.ifBlank { scoreboardDefaults.serverName },
            title = resolvedTitle,
            animateTitle = animateTitle,
            lines = if (lines.isEmpty()) scoreboardDefaults.lines else lines,
            lobbyTitle = lobbyTitle,
            lobbyLines = if (lobbyLines.isEmpty()) scoreboardDefaults.lobbyLines else lobbyLines
        )
    }

    private fun readMigratedString(
        jsonObject: JsonObject,
        key: String,
        fallback: String,
        migration: MigrationTracker
    ): String {
        return if (jsonObject.has(key)) {
            migrateLegacy(jsonObject[key].asString, migration)
        } else {
            fallback
        }
    }

    private fun readBoolean(jsonObject: JsonObject, key: String, fallback: Boolean): Boolean {
        return if (jsonObject.has(key)) {
            jsonObject[key].asBoolean
        } else {
            fallback
        }
    }

    private fun readLines(
        jsonObject: JsonObject,
        key: String,
        fallback: List<String>,
        migration: MigrationTracker,
        gson: Gson
    ): List<String> {
        if (!jsonObject.has(key) || !jsonObject[key].isJsonArray) {
            return fallback
        }

        return gson.fromJson(jsonObject[key], Array<String>::class.java)
            ?.map { migrateLegacy(it, migration) }
            ?: fallback
    }

    private fun readLobbyLines(
        scoreboardObject: JsonObject,
        scoreboardDefaults: ScoreboardConfig,
        migration: MigrationTracker,
        gson: Gson
    ): List<String> {
        if (!scoreboardObject.has("lobby_lines") || !scoreboardObject["lobby_lines"].isJsonArray) {
            return scoreboardDefaults.lobbyLines
        }

        val lines = gson.fromJson(scoreboardObject["lobby_lines"], Array<String>::class.java)
            ?.map { migrateLegacy(it, migration) }
            ?: scoreboardDefaults.lobbyLines

        val hasCoinsPlaceholder = lines.any { it.contains("{coins}") }
        val hasGuildPlaceholder = lines.any { it.contains("{guild}") }

        return if (!hasCoinsPlaceholder || !hasGuildPlaceholder) {
            migration.migrated = true
            scoreboardDefaults.lobbyLines
        } else {
            lines
        }
    }

    private fun migrateLegacy(text: String, tracker: MigrationTracker): String {
        val normalized = text.replace("Â§", "§")
        if (!LEGACY_CODE_REGEX.containsMatchIn(normalized)) {
            return normalized
        }

        tracker.migrated = true
        val component = if (normalized.contains('§')) {
            LEGACY_SECTION.deserialize(normalized)
        } else {
            LEGACY_AMPERSAND.deserialize(normalized)
        }
        return MINI_MESSAGE.serialize(component)
    }

    private val MINI_MESSAGE = MiniMessage.miniMessage()
    private val LEGACY_SECTION = LegacyComponentSerializer.legacySection()
    private val LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand()
    private val LEGACY_CODE_REGEX = Regex("(?i)[&§][0-9A-FK-OR]")
}
