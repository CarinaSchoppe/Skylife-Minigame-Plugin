package com.carinaschoppe.skylife.utility.messages

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import java.io.File

object MessageLoader {

    fun saveMessages() {
        val file = File(Bukkit.getServer().pluginsFolder, "Skylife/messages.json")

        if (!file.exists()) {
            file.createNewFile()
        }

        val gson: Gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component::class.java, ComponentTypeAdapter(MiniMessage.miniMessage()))
            .create()

        val json: String = gson.toJson(Messages.instance)
        file.writeText(json)
        Bukkit.getServer().sendMessage(Messages.instance.PREFIX.append(Component.text("Messages saved!", Messages.instance.MESSAGE_COLOR)))

    }

    fun loadMessages() {
        val file = File(Bukkit.getServer().pluginsFolder, "Skylife/messages.json")


        if (!file.exists()) {
            saveMessages()
        }


        val gson: Gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component::class.java, ComponentTypeAdapter(MiniMessage.miniMessage()))
            .create()

        val json: String = file.readText()
        val messages: Messages = gson.fromJson(json, Messages::class.java)

        Messages.instance = messages


        Bukkit.getServer().sendMessage(Messages.instance.PREFIX.append(Component.text("Messages loaded!", Messages.instance.MESSAGE_COLOR)))
    }

}