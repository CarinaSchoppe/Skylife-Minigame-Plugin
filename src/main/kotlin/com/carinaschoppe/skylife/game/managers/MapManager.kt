package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.game.Game
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.WorldCreator
import java.io.File

object MapManager {

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

    fun locationWorldConverter(location: Location, game: Game): Location {
        return Location(Bukkit.getWorld(game.gameID.toString()), location.x, location.y, location.z, location.yaw, location.pitch)
    }

    fun unloadWorld(game: Game): Boolean {
        val world = Bukkit.getWorld(game.gameID.toString()) ?: return false
        // Stelle sicher, dass alle Spieler aus der Welt teleportiert werden, bevor du sie entlädst
        // Entlade die Welt
        Bukkit.unloadWorld(world, false)

        val folder = File(Bukkit.getServer().worldContainer, world.name)
        if (folder.exists()) {
            return folder.deleteRecursively()
        }
        return false
    }
}