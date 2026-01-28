package com.carinaschoppe.skylife.utility.messages

import com.carinaschoppe.skylife.Skylife
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.io.File

/**
 * Handles loading and saving of plugin messages to/from a JSON configuration file.
 *
 * This utility object provides methods to save the current in-memory messages to disk
 * and load them back. Messages are stored in JSON format with support for MiniMessage formatting.
 * The messages are stored in the plugin's data folder as 'messages.json'.
 */
object MessageLoader {

    /**
     * Saves all message templates to messages.json configuration file.
     *
     * This method:
     * 1. Creates the messages.json file if it doesn't exist
     * 2. Serializes the Templates data class to JSON
     * 3. Writes the JSON to the file with pretty printing
     * 4. Sends a confirmation message to the console
     *
     * @see Templates
     * @see Messages
     */
    fun saveMessages() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "messages.json")

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        if (!file.exists()) {
            file.createNewFile()
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        // Serialize the Templates object
        val json: String = gson.toJson(Messages.TEMPLATES)
        file.writeText(json)
        Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Messages saved!", Messages.MESSAGE_COLOR)))

    }


    /**
     * Loads message templates from messages.json into the Messages.TEMPLATES object.
     *
     * This method:
     * 1. Checks if messages.json exists, creates it with default values if not
     * 2. Deserializes the JSON into a Templates object
     * 3. Updates the Messages.TEMPLATES property
     * 4. Sends a confirmation message to the console
     *
     * @see Templates
     * @see Messages
     */
    fun loadMessages() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "messages.json")

        if (!file.exists()) {
            saveMessages()
            return
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        val json: String = file.readText()

        try {
            // Deserialize JSON into Templates object
            val templates = gson.fromJson(json, Templates::class.java)
            if (templates != null) {
                Messages.TEMPLATES = templates
                Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Messages loaded!", Messages.MESSAGE_COLOR)))
            }
        } catch (e: Exception) {
            Bukkit.getLogger().warning("Failed to load messages from messages.json: ${e.message}")
            Bukkit.getLogger().warning("Using default messages instead.")
        }
    }

}
