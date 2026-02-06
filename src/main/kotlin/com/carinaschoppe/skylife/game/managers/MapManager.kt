package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.platform.PluginContext
import org.bukkit.Bukkit
import org.bukkit.GameRules
import org.bukkit.World
import org.bukkit.WorldCreator
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
        File(PluginContext.plugin.dataFolder, "maps").also { it.mkdirs() }
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
            val startTime = System.currentTimeMillis()

            // Copy map template to server worlds folder with timeout monitoring
            copyDirectory(templateFolder, worldFolder)
            val copyTime = System.currentTimeMillis() - startTime

            if (copyTime > 10000) { // 10 seconds warning
                Bukkit.getLogger().warning("[MapManager] Map copy took ${copyTime}ms for template '$template'")
            }

            // Delete uid.dat to prevent world UID conflicts
            File(worldFolder, "uid.dat").delete()

            // Load the world (Paper 1.21+ compatible) with timeout check
            val loadStartTime = System.currentTimeMillis()
            val world = Bukkit.createWorld(
                WorldCreator(worldName)
                    .environment(World.Environment.NORMAL)
                    .generateStructures(false)
            )
            val loadTime = System.currentTimeMillis() - loadStartTime

            if (loadTime > 30000) { // 30 seconds warning
                Bukkit.getLogger().warning("[MapManager] World loading took ${loadTime}ms for world '$worldName'")
            }

            if (world != null) {
                activeWorlds[gameID] = worldName
                val totalTime = System.currentTimeMillis() - startTime
                Bukkit.getLogger().info("[MapManager] Loaded map '$template' as world '$worldName' for game $gameID (${totalTime}ms)")

                // Set world properties (Paper 1.21+ GameRule API)
                world.setGameRule(GameRules.ADVANCE_TIME, false)
                world.setGameRule(GameRules.ADVANCE_WEATHER, false)
                world.setGameRule(GameRules.SPAWN_MOBS, false)
                world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false)
                world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0)
                world.time = 6000 // Set to noon

                return world
            } else {
                Bukkit.getLogger().severe("[MapManager] Failed to load world '$worldName' for game $gameID")
                // Cleanup failed world folder
                deleteDirectory(worldFolder)
                return null
            }
        } catch (e: Exception) {
            Bukkit.getLogger().severe("[MapManager] Error loading map for game $gameID: ${e.message}")
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
            Bukkit.getLogger().warning("[MapManager] No active world found for game $gameID")
            return
        }

        try {
            val world = Bukkit.getWorld(worldName)
            if (world == null) {
                Bukkit.getLogger().warning("[MapManager] World '$worldName' not found for game $gameID")
                activeWorlds.remove(gameID)
                return
            }

            // Unload the world
            val unloaded = Bukkit.unloadWorld(world, false) // Don't save changes
            if (!unloaded) {
                Bukkit.getLogger().severe("[MapManager] Failed to unload world '$worldName' for game $gameID")
                // Still remove from activeWorlds to prevent memory leak
                activeWorlds.remove(gameID)
                return
            }

            // Delete the world folder
            val worldFolder = File(Bukkit.getWorldContainer(), worldName)
            deleteDirectory(worldFolder)
            Bukkit.getLogger().info("[MapManager] Deleted world '$worldName' for game $gameID")
        } catch (e: Exception) {
            Bukkit.getLogger().severe("[MapManager] Error during world cleanup for game $gameID: ${e.message}")
            e.printStackTrace()
        } finally {
            // Always remove from activeWorlds to prevent memory leaks
            activeWorlds.remove(gameID)
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
        Bukkit.getLogger().info("[MapManager] Cleaning up ${activeWorlds.size} active game worlds...")
        activeWorlds.keys.toList().forEach { gameID ->
            unloadAndDeleteWorld(gameID)
        }
        Bukkit.getLogger().info("[MapManager] World cleanup completed")
    }

    /**
     * Cleans up orphaned game worlds on startup.
     * Finds and deletes any world folders matching "game_*" pattern that weren't properly cleaned up.
     */
    fun cleanupOrphanedWorlds() {
        val worldContainer = Bukkit.getWorldContainer()
        val orphanedWorlds = worldContainer.listFiles { file ->
            file.isDirectory && file.name.startsWith("game_")
        } ?: return

        if (orphanedWorlds.isEmpty()) {
            return
        }

        Bukkit.getLogger().warning("[MapManager] Found ${orphanedWorlds.size} orphaned game worlds from previous sessions")

        orphanedWorlds.forEach { worldFolder ->
            try {
                // Try to unload world if it's loaded
                val world = Bukkit.getWorld(worldFolder.name)
                if (world != null) {
                    Bukkit.unloadWorld(world, false)
                }

                // Delete the folder
                deleteDirectory(worldFolder)
                Bukkit.getLogger().info("[MapManager] Cleaned up orphaned world: ${worldFolder.name}")
            } catch (e: Exception) {
                Bukkit.getLogger().severe("[MapManager] Failed to cleanup orphaned world ${worldFolder.name}: ${e.message}")
            }
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
