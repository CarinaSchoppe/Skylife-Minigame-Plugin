package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.Skylife
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

/**
 * Manages map loading, copying, and cleanup for multi-game support.
 * Each game gets its own world copy that is cleaned up after the game ends.
 */
object MapManager {

    private val mapsFolder: File by lazy {
        File(Skylife.instance.dataFolder, "maps").also { it.mkdirs() }
    }

    private val activeWorlds = mutableMapOf<UUID, String>() // gameID -> worldName

    /**
     * Checks if the maps folder contains any template maps.
     *
     * @return true if at least one map template exists
     */
    fun hasAvailableMaps(): Boolean {
        return mapsFolder.listFiles()?.any { it.isDirectory } ?: false
    }

    /**
     * Gets a list of all available map templates.
     *
     * @return List of map template folder names
     */
    fun getAvailableMaps(): List<String> {
        return mapsFolder.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
    }

    /**
     * Loads a copy of a map template for a specific game.
     * The world is copied from the maps folder and loaded as a new world.
     *
     * @param gameID The unique ID of the game
     * @param templateMapName The name of the template map in the maps folder
     * @return The loaded world, or null if loading failed
     */
    fun loadMapForGame(gameID: UUID, templateMapName: String? = null): World? {
        val template = templateMapName ?: getRandomMap() ?: run {
            Bukkit.getLogger().severe("No map templates available in ${mapsFolder.absolutePath}")
            return null
        }

        val templateFolder = File(mapsFolder, template)
        if (!templateFolder.exists() || !templateFolder.isDirectory) {
            Bukkit.getLogger().severe("Map template '$template' not found in maps folder")
            return null
        }

        // Generate unique world name for this game instance
        val worldName = "game_${gameID}_${System.currentTimeMillis()}"
        val worldFolder = File(Bukkit.getWorldContainer(), worldName)

        try {
            // Copy map template to server worlds folder
            copyDirectory(templateFolder, worldFolder)

            // Delete uid.dat to prevent world UID conflicts
            File(worldFolder, "uid.dat").delete()

            // Load the world
            val world = Bukkit.createWorld(
                WorldCreator(worldName)
                    .type(WorldType.NORMAL)
                    .generateStructures(false)
            )

            if (world != null) {
                activeWorlds[gameID] = worldName
                Bukkit.getLogger().info("Loaded map '$template' as world '$worldName' for game $gameID")

                // Set world properties
                world.setGameRuleValue("doDaylightCycle", "false")
                world.setGameRuleValue("doWeatherCycle", "false")
                world.setGameRuleValue("doMobSpawning", "false")
                world.setGameRuleValue("announceAdvancements", "false")
                world.time = 6000 // Set to noon

                return world
            } else {
                Bukkit.getLogger().severe("Failed to load world '$worldName' for game $gameID")
                // Cleanup failed world folder
                deleteDirectory(worldFolder)
                return null
            }
        } catch (e: Exception) {
            Bukkit.getLogger().severe("Error loading map for game $gameID: ${e.message}")
            e.printStackTrace()
            // Cleanup on error
            deleteDirectory(worldFolder)
            return null
        }
    }

    /**
     * Unloads and deletes the world associated with a game.
     * This should be called after the game ends and all players have been teleported out.
     *
     * @param gameID The unique ID of the game
     */
    fun unloadAndDeleteWorld(gameID: UUID) {
        val worldName = activeWorlds[gameID] ?: run {
            Bukkit.getLogger().warning("No active world found for game $gameID")
            return
        }

        val world = Bukkit.getWorld(worldName)
        if (world == null) {
            Bukkit.getLogger().warning("World '$worldName' not found for game $gameID")
            activeWorlds.remove(gameID)
            return
        }

        // Unload the world
        val unloaded = Bukkit.unloadWorld(world, false) // Don't save changes
        if (!unloaded) {
            Bukkit.getLogger().severe("Failed to unload world '$worldName' for game $gameID")
            return
        }

        // Delete the world folder
        val worldFolder = File(Bukkit.getWorldContainer(), worldName)
        try {
            deleteDirectory(worldFolder)
            activeWorlds.remove(gameID)
            Bukkit.getLogger().info("Deleted world '$worldName' for game $gameID")
        } catch (e: Exception) {
            Bukkit.getLogger().severe("Error deleting world '$worldName': ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Gets the world associated with a specific game.
     *
     * @param gameID The unique ID of the game
     * @return The world, or null if not found
     */
    fun getWorldForGame(gameID: UUID): World? {
        val worldName = activeWorlds[gameID] ?: return null
        return Bukkit.getWorld(worldName)
    }

    /**
     * Gets a random map template from the maps folder.
     *
     * @return A random map name, or null if no maps available
     */
    private fun getRandomMap(): String? {
        val maps = getAvailableMaps()
        return if (maps.isNotEmpty()) maps.random() else null
    }

    /**
     * Recursively copies a directory.
     *
     * @param source The source directory
     * @param target The target directory
     */
    private fun copyDirectory(source: File, target: File) {
        if (!target.exists()) {
            target.mkdirs()
        }

        source.listFiles()?.forEach { file ->
            val targetFile = File(target, file.name)

            // Skip session.lock file
            if (file.name == "session.lock") {
                return@forEach
            }

            if (file.isDirectory) {
                copyDirectory(file, targetFile)
            } else {
                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    /**
     * Recursively deletes a directory and all its contents.
     *
     * @param directory The directory to delete
     */
    private fun deleteDirectory(directory: File) {
        if (!directory.exists()) return

        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        directory.delete()
    }

    /**
     * Cleans up all active game worlds.
     * This should only be called on plugin disable.
     */
    fun cleanupAllWorlds() {
        activeWorlds.keys.toList().forEach { gameID ->
            unloadAndDeleteWorld(gameID)
        }
    }

    /**
     * Converts a location to use the game's dedicated world.
     *
     * @param location The original location
     * @param game The game instance with the target world
     * @return A new location with the same coordinates but in the game's world
     */
    fun locationWorldConverter(location: org.bukkit.Location, game: com.carinaschoppe.skylife.game.Game): org.bukkit.Location {
        val world = game.gameWorld ?: game.lobbyLocation.world
        return org.bukkit.Location(
            world,
            location.x,
            location.y,
            location.z,
            location.yaw,
            location.pitch
        )
    }
}
