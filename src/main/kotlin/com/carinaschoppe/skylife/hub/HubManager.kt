package com.carinaschoppe.skylife.hub

import com.carinaschoppe.skylife.Skylife
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

/**
 * Manages the hub spawn location where players spawn on server join
 * and are teleported when leaving games.
 */
object HubManager {

    private var hubSpawn: Location? = null
    private val configFile = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "hub.yml")
    private lateinit var config: YamlConfiguration

    /**
     * Loads the hub spawn location from configuration.
     * Should be called on plugin startup.
     */
    fun loadHubSpawn() {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
        }

        config = YamlConfiguration.loadConfiguration(configFile)

        // Load hub spawn if configured
        if (config.contains("hub.world") &&
            config.contains("hub.x") &&
            config.contains("hub.y") &&
            config.contains("hub.z")
        ) {

            val worldName = config.getString("hub.world")
            val world = Bukkit.getWorld(worldName!!)

            if (world != null) {
                val x = config.getDouble("hub.x")
                val y = config.getDouble("hub.y")
                val z = config.getDouble("hub.z")
                val yaw = config.getDouble("hub.yaw", 0.0).toFloat()
                val pitch = config.getDouble("hub.pitch", 0.0).toFloat()

                hubSpawn = Location(world, x, y, z, yaw, pitch)
            }
        }
    }

    /**
     * Sets the hub spawn location to the specified location.
     * @param location The new hub spawn location
     */
    fun setHubSpawn(location: Location) {
        hubSpawn = location

        // Save to config
        config.set("hub.world", location.world.name)
        config.set("hub.x", location.x)
        config.set("hub.y", location.y)
        config.set("hub.z", location.z)
        config.set("hub.yaw", location.yaw.toDouble())
        config.set("hub.pitch", location.pitch.toDouble())

        config.save(configFile)
    }

    /**
     * Gets the current hub spawn location.
     * @return The hub spawn location, or null if not set
     */
    fun getHubSpawn(): Location? = hubSpawn

    /**
     * Teleports a player to the hub spawn.
     * If no hub spawn is set, teleports to world spawn.
     * @param player The player to teleport
     */
    fun teleportToHub(player: Player) {
        val spawn = hubSpawn
        if (spawn != null) {
            player.teleport(spawn)
        } else {
            // Fallback to world spawn if hub not configured
            val world = Bukkit.getWorlds().firstOrNull()
            if (world != null) {
                player.teleport(world.spawnLocation)
            }
        }
    }

    /**
     * Checks if hub spawn has been configured.
     * @return true if hub spawn is set, false otherwise
     */
    fun isHubSpawnSet(): Boolean = hubSpawn != null
}
