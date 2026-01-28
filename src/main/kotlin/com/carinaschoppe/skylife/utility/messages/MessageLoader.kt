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

        // Serialize the Templates object by extracting all its fields into a map
        val templatesMap = mutableMapOf<String, String>()
        Templates::class.java.declaredFields.forEach { field ->
            field.isAccessible = true
            val value = field.get(Templates)
            if (value is String) {
                templatesMap[field.name] = value
            }
        }

        val json: String = gson.toJson(templatesMap)
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
            // Deserialize JSON into a map and manually assign to Templates object
            val jsonMap = gson.fromJson<Map<String, String>>(json, object : com.google.gson.reflect.TypeToken<Map<String, String>>() {}.type)

            if (jsonMap != null) {
                // Use reflection to set all properties
                Templates::class.java.declaredFields.forEach { field ->
                    field.isAccessible = true
                    val value = jsonMap[field.name]
                    if (value != null && field.type == String::class.java) {
                        field.set(Templates, value)
                    }
                }
                Bukkit.getServer().consoleSender.sendMessage(Messages.PREFIX.append(Component.text("Messages loaded!", Messages.MESSAGE_COLOR)))
            }
        } catch (e: Exception) {
            Bukkit.getLogger().warning("Failed to load messages from messages.json: ${e.message}")
            Bukkit.getLogger().warning("Using default messages instead.")
        }
    }

}
