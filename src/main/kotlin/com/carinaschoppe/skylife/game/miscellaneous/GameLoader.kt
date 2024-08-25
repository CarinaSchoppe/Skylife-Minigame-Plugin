package com.carinaschoppe.skylife.game.miscellaneous

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.GamePattern
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import java.io.File

object GameLoader {


    fun loadGameFromFile(file: File) {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val json = file.readText()
        val gamePattern = gson.fromJson(json, GamePattern::class.java)
        GameCluster.gamePatterns.add(gamePattern)
    }


    fun deleteGameFile(gamePattern: GamePattern) {
        val folder = File(Bukkit.getServer().pluginsFolder, "/Skylife/games")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File(folder, "${gamePattern.mapName}.json")
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveGameToFile(gamePattern: GamePattern) {
        val folder = File(Bukkit.getServer().pluginsFolder, "/Skylife/games")
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
        val folder = File(Bukkit.getServer().pluginsFolder, "/Skylife/games")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val files = mutableListOf<File>()
        File(Bukkit.getServer().pluginsFolder, "/Skylife/games").listFiles { _, name -> name.endsWith(".json") }?.let { files.addAll(it) }
        return files
    }

}