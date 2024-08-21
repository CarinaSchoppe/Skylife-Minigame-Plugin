package com.carinaschoppe.skylife.game.miscellaneous

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.GamePattern
import com.google.gson.Gson
import java.io.File

object GameLoader {


    fun loadGameFromFile(file: File) {
        val gson = Gson()
        val json = file.readText()
        val gamePattern = gson.fromJson(json, GamePattern::class.java)
        GameCluster.gamePatterns.add(gamePattern)
    }


    fun deleteGameFile(gamePattern: GamePattern) {

        val folder = File("/Skylife/games")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File(folder, "${gamePattern.mapName}.json")
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveGameToFile(gamePattern: GamePattern) {

        val folder = File("/Skylife/games")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File(folder, "${gamePattern.mapName}.json")
        if (file.exists()) {
            file.delete()
        }
        val gson = Gson()
        val json = gson.toJson(gamePattern)
        file.writeText(json)
    }


    fun findAllGames(): List<File> {
        val files = mutableListOf<File>()
        File("/Skylife/games").listFiles { _, name -> name.endsWith(".json") }?.let { files.addAll(it) }
        return files
    }

}