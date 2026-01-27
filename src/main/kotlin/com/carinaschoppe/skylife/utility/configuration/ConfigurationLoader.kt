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
        val scoreboardDefaults = ScoreboardConfig()
        val migration = MigrationTracker()

        if (jsonObject.has("scoreboard_title")) {
            defaults.scoreboardTitle = migrateLegacy(jsonObject.get("scoreboard_title").asString, migration)
        }

        if (jsonObject.has("timer_settings")) {
            defaults.timer = gson.fromJson(jsonObject.get("timer_settings"), Timer::class.java)
        }

        if (jsonObject.has("scoreboard") && jsonObject.get("scoreboard").isJsonObject) {
            val scoreboardObject = jsonObject.getAsJsonObject("scoreboard")
            val serverName = if (scoreboardObject.has("server_name")) {
                migrateLegacy(scoreboardObject.get("server_name").asString, migration)
            } else {
                scoreboardDefaults.serverName
            }
            val title = if (scoreboardObject.has("title")) {
                migrateLegacy(scoreboardObject.get("title").asString, migration)
            } else {
                ""
            }
            val animateTitle = if (scoreboardObject.has("animate_title")) scoreboardObject.get("animate_title").asBoolean else scoreboardDefaults.animateTitle
            val lines = if (scoreboardObject.has("lines") && scoreboardObject.get("lines").isJsonArray) {
                gson.fromJson(scoreboardObject.get("lines"), Array<String>::class.java)
                    ?.map { migrateLegacy(it, migration) }
                    ?: scoreboardDefaults.lines
            } else {
                scoreboardDefaults.lines
            }

            val resolvedTitle = if (title.isBlank()) defaults.scoreboardTitle else title
            defaults.scoreboard = ScoreboardConfig(
                serverName = serverName.ifBlank { scoreboardDefaults.serverName },
                title = resolvedTitle,
                animateTitle = animateTitle,
                lines = if (lines.isEmpty()) scoreboardDefaults.lines else lines
            )
        } else {
            defaults.scoreboard = scoreboardDefaults.copy(title = defaults.scoreboardTitle)
        }

        return ConfigMigrationResult(defaults, migration.migrated)
    }

    private data class MigrationTracker(var migrated: Boolean = false)

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
