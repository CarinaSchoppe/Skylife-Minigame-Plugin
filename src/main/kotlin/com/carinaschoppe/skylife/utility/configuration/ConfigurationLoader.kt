package com.carinaschoppe.skylife.utility.configuration

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.utility.messages.Messages
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.io.File

object ConfigurationLoader {


    fun saveConfiguration() {

        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "config.json")

        if (!file.exists()) {
            file.createNewFile()
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        val json: String = gson.toJson(Configurations.instance)
        file.writeText(json)
        Bukkit.getServer().consoleSender.sendMessage(Messages.instance.PREFIX.append(Component.text("Configuration saved!", Messages.instance.MESSAGE_COLOR)))

    }

    fun loadConfiguration() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "config.json")

        if (!file.exists()) {
            saveConfiguration()
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        val json: String = file.readText()
        val config: Configurations = gson.fromJson(json, Configurations::class.java)

        Configurations.instance = config
        Bukkit.getServer().consoleSender.sendMessage(Messages.instance.PREFIX.append(Component.text("Configuration loaded!", Messages.instance.MESSAGE_COLOR)))
    }


}