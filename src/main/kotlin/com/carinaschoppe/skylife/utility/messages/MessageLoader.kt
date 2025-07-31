package com.carinaschoppe.skylife.utility.messages

import com.carinaschoppe.skylife.Skylife
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import java.io.File

object MessageLoader {

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


    //TODO: Why not in use
    fun loadMessages() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "messages.json")


        if (!file.exists()) {
            saveMessages()
        }


        val gson: Gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component::class.java, ComponentTypeAdapter(MiniMessage.miniMessage()))
            .create()

        val json: String = file.readText()
        val messages: Messages = gson.fromJson(json, Messages::class.java)


        Bukkit.getServer().sendMessage(Messages.PREFIX.append(Component.text("Messages loaded!", Messages.MESSAGE_COLOR)))
    }

}