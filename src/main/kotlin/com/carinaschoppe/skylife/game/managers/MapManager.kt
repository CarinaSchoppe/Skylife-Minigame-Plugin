package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.game.Game
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.WorldCreator
import java.io.File

/**
 * Manages the loading, unloading, and conversion of game world maps.
 *
 * This object handles the creation of isolated world instances for each game,
 * ensuring that each game session has its own copy of the map. It also provides
 * utilities for converting locations between different world contexts.
 */
object MapManager {

    /**
     * Loads a game world by creating a copy of the template map.
     *
     * This method:
     * 1. Locates the template map in the 'game_maps' directory
     * 2. Creates a unique copy for the game using its gameID
     * 3. Loads the world using Bukkit's WorldCreator
     *
     * @param game The game instance that needs its world loaded
     * @throws IllegalStateException if the source map directory doesn't exist
     */
    fun loadGameWorld(game: Game) {
        val source = File(Bukkit.getServer().worldContainer, "game_maps/${game.pattern.mapName}")
        if (!source.exists()) {
            throw IllegalStateException("Source map '${game.pattern.mapName}' not found in game_maps directory")
        }

        val worldName = game.gameID.toString()
        val destination = File(Bukkit.getServer().worldContainer, worldName)

        // Clean up existing world if it exists
        if (destination.exists()) {
            Bukkit.unloadWorld(worldName, false)
            destination.deleteRecursively()
        }

        // Copy the template world
        source.copyRecursively(destination, true)

        // Create and load the new world
        val worldCreator = WorldCreator(worldName)
        Bukkit.createWorld(worldCreator)

        Bukkit.getServer().consoleSender.sendMessage(
            Component.text("Loaded map '${game.pattern.mapName}' as world '$worldName'")
        )
    }

    /**
     * Converts a location from the template world to the actual game world.
     *
     * This is used to translate locations defined in the game pattern (which are in the template world)
     * to the actual game world that was created for this specific game instance.
     *
     * @param location The original location in the template world
     * @param game The game instance containing the target world
     * @return A new Location object in the game's world with the same coordinates and rotation
     */
    fun locationWorldConverter(location: Location, game: Game): Location {
        return Location(
            Bukkit.getWorld(game.gameID.toString()),
            location.x,
            location.y,
            location.z,
            location.yaw,
            location.pitch
        )
    }

    /**
     * Unloads and deletes a game world when it's no longer needed.
     *
     * This method:
     * 1. Unloads the world from the Bukkit server
     * 2. Deletes the world folder from disk
     *
     * @param game The game whose world should be unloaded
     * @return `true` if the world was successfully unloaded and deleted, `false` otherwise
     */
    fun unloadWorld(game: Game): Boolean {
        val world = Bukkit.getWorld(game.gameID.toString()) ?: return false

        // Note: Ensure all players have been teleported out before calling this
        val unloaded = Bukkit.unloadWorld(world, false)
        if (!unloaded) return false

        // Delete the world folder
        val folder = File(Bukkit.getServer().worldContainer, world.name)
        return if (folder.exists()) {
            folder.deleteRecursively()
        } else {
            false
        }
    }
}
