package com.carinaschoppe.skylife.game.miscellaneous

import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import java.io.File

object MapLoader {

    fun loadGameWorld(game: Game) {
        val source = File(Bukkit.getServer().worldContainer, "game_maps/${game.gamePattern.mapName}")
        if (source.exists()) {
            Bukkit.getServer().consoleSender.sendMessage("Found Map: '${game.gamePattern.mapName}'")
        }
        val worldName = game.gameID.toString()
        val destination = File(Bukkit.getServer().worldContainer, worldName)
        Bukkit.getServer().consoleSender.sendMessage(destination.absolutePath)


        Bukkit.getServer().consoleSender.sendMessage("Starting Map: '${game.gamePattern.mapName}'")

        if (destination.exists()) {
            Bukkit.unloadWorld(worldName, false)
            destination.deleteRecursively()
        }
        source.copyRecursively(destination, true)


        Bukkit.getServer().consoleSender.sendMessage("Copied Map: '${game.gamePattern.mapName}'")



        val worldCreator = WorldCreator(worldName)
        Bukkit.createWorld(worldCreator)

        Bukkit.getServer().consoleSender.sendMessage("Loaded Map: '${game.gamePattern.mapName}'")
    }


}