package com.carinaschoppe.skylife.utility.messages

import com.carinaschoppe.skylife.Skylife
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
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
     * Saves all messages from the [Messages] object to a JSON configuration file.
     *
     * This method:
     * 1. Creates the messages.json file if it doesn't exist
     * 2. Configures Gson with pretty printing and a custom ComponentTypeAdapter
     * 3. Serializes the Messages object to JSON
     * 4. Writes the JSON to the file
     * 5. Sends a confirmation message to the console
     *
     * @see ComponentTypeAdapter
     * @see Messages
     */
    fun saveMessages() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "messages.json")

        if (!file.exists()) {
            file.createNewFile()
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component::class.java, ComponentTypeAdapter(MiniMessage.miniMessage()))
            .create()

        val json: String = gson.toJson(Messages)
        file.writeText(json)
        Bukkit.getServer().sendMessage(Messages.PREFIX.append(Component.text("Messages saved!", Messages.MESSAGE_COLOR)))

    }


    /**
     * Loads messages from the JSON configuration file into the [Messages] object.
     *
     * This method:
     * 1. Checks if messages.json exists, creates it with default values if not
     * 2. Configures Gson with a custom ComponentTypeAdapter for MiniMessage support
     * 3. Deserializes the JSON into the Messages object
     * 4. Sends a confirmation message to the console
     *
     * Note: This method is currently not in use in the codebase. Consider whether
     * it should be used to load message customizations on plugin startup.
     *
     * @see ComponentTypeAdapter
     * @see Messages
     */
    fun loadMessages() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "messages.json")


        if (!file.exists()) {
            saveMessages()
        }


        val gson: Gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component::class.java, ComponentTypeAdapter(MiniMessage.miniMessage()))
            .create()

        val json: String = file.readText()
        gson.fromJson(json, Messages::class.java)


        Bukkit.getServer().sendMessage(Messages.PREFIX.append(Component.text("Messages loaded!", Messages.MESSAGE_COLOR)))
    }

}