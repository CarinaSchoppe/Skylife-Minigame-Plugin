package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.Skylife.Companion.folderLocation
import com.carinaschoppe.skylife.utility.messages.Messages
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.io.File

/**
 * Handles the loading, saving, and management of game pattern files.
 *
 * This object is responsible for all file I/O operations related to game patterns,
 * including loading patterns from disk, saving them, and finding available patterns.
 * It uses JSON for serialization of game patterns.
 */
object GameLoader {

    /**
     * The directory where game pattern files are stored, relative to the server's plugins folder.
     */
    val gamesFolder = folderLocation + "games"

    /**
     * Loads a game pattern from a JSON file and adds it to the GameCluster.
     *
     * @param file The JSON file containing the game pattern data
     * @throws com.google.gson.JsonSyntaxException if the JSON is malformed
     * @throws com.google.gson.JsonParseException if the JSON cannot be parsed into a GamePattern
     */
    fun loadGameFromFile(file: File) {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val json = file.readText()
        val gamePattern = gson.fromJson(json, GamePattern::class.java)
        GameCluster.addGamePattern(gamePattern)

        // Create a game instance from the pattern if it's complete
        if (gamePattern.isComplete()) {
            try {
                GameCluster.createGameFromPattern(gamePattern)
                Bukkit.getServer().consoleSender.sendMessage(
                    Messages.PREFIX
                        .append(Component.text("Game '", Messages.MESSAGE_COLOR))
                        .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                        .append(Component.text("' loaded and game instance created!", Messages.MESSAGE_COLOR))
                )
            } catch (e: IllegalStateException) {
                Bukkit.getServer().consoleSender.sendMessage(
                    Messages.PREFIX
                        .append(Component.text("Game pattern '", Messages.MESSAGE_COLOR))
                        .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                        .append(Component.text("' loaded, but failed to create game instance: ", Messages.ERROR_COLOR))
                        .append(Component.text(e.message ?: "Unknown error", Messages.ERROR_COLOR))
                )
            }
        } else {
            Bukkit.getServer().consoleSender.sendMessage(
                Messages.PREFIX
                    .append(Component.text("Game pattern '", Messages.MESSAGE_COLOR))
                    .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                    .append(Component.text("' loaded, but is incomplete!", Messages.ERROR_COLOR))
            )
        }
    }

    /**
     * Deletes the JSON file associated with a game pattern.
     *
     * @param gamePattern The game pattern whose file should be deleted
     */
    fun deleteGameFile(gamePattern: GamePattern) {
        val folder = File(Bukkit.getServer().pluginsFolder, gamesFolder).also {
            if (!it.exists()) it.mkdirs()
        }
        File(folder, "${gamePattern.mapName}.json").takeIf { it.exists() }?.delete()
    }

    /**
     * Saves a game pattern to a JSON file.
     *
     * If a file with the same name already exists, it will be overwritten.
     *
     * @param gamePattern The game pattern to save
     */
    fun saveGameToFile(gamePattern: GamePattern) {
        val folder = File(Bukkit.getServer().pluginsFolder, gamesFolder).also {
            if (!it.exists()) it.mkdirs()
        }
        val file = File(folder, "${gamePattern.mapName}.json")

        // Delete existing file if it exists
        file.takeIf { it.exists() }?.delete()

        // Write the new file
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(gamePattern)
        file.writeText(json)
    }

    /**
     * Finds all game pattern files in the games directory.
     *
     * @return A list of JSON files containing game patterns
     */
    fun findAllGames(): List<File> {
        val folder = File(Bukkit.getServer().pluginsFolder, gamesFolder).also {
            if (!it.exists()) it.mkdirs()
        }

        return folder.listFiles { _, name ->
            name.endsWith(".json")
        }?.toList() ?: emptyList()
    }
}
