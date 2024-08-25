package com.carinaschoppe.skylife.game.miscellaneous

import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import java.io.File

object MapLoader {

    fun loadGameWorld(game: Game) {
        val source = File(Bukkit.getServer().worldContainer, "game_maps/${game.gamePattern.mapName}")
        val worldName = game.gameID.toString()
        val destination = File(Bukkit.getServer().worldContainer, worldName)

        if (destination.exists()) {
            Bukkit.unloadWorld(worldName, false)
            destination.deleteRecursively()
        }
        source.copyRecursively(destination, true)

        val worldCreator = WorldCreator(worldName)
        Bukkit.createWorld(worldCreator)
    }


}