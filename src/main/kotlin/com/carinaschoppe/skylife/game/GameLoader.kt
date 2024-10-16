package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.Skylife.Companion.folderLocation
import com.carinaschoppe.skylife.utility.messages.Messages
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.io.File

object GameLoader {

    val gamesFolder = folderLocation + "games"

    fun loadGameFromFile(file: File) {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val json = file.readText()
        val gamePattern = gson.fromJson(json, GamePattern::class.java)
        GameCluster.gamePatterns.add(gamePattern)
        Bukkit.getServer().consoleSender.sendMessage(Messages.instance.PREFIX.append(Component.text("The Game: '", Messages.instance.MESSAGE_COLOR)).append(Component.text(gamePattern.mapName, Messages.instance.NAME_COLOR)).append(Component.text("' has been loaded!", Messages.instance.MESSAGE_COLOR)))
    }


    fun deleteGameFile(gamePattern: GamePattern) {
        val folder = File(Bukkit.getServer().pluginsFolder, gamesFolder)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File(folder, "${gamePattern.mapName}.json")
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveGameToFile(gamePattern: GamePattern) {
        val folder = File(Bukkit.getServer().pluginsFolder, gamesFolder)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File(folder, "${gamePattern.mapName}.json")
        if (file.exists()) {
            file.delete()
        }
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(gamePattern)
        file.writeText(json)
    }


    fun findAllGames(): List<File> {
        //create game folder
        val folder = File(Bukkit.getServer().pluginsFolder, gamesFolder)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val files = mutableListOf<File>()
        File(Bukkit.getServer().pluginsFolder, gamesFolder).listFiles { _, name -> name.endsWith(".json") }?.let { files.addAll(it) }
        return files
    }

}