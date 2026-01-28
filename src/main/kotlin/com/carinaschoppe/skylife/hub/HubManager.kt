package com.carinaschoppe.skylife.hub

import com.carinaschoppe.skylife.utility.location.LocationManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Manages the hub spawn location where players spawn on server join
 * and are teleported when leaving games.
 */
object HubManager {

    private const val HUB_LOCATION_NAME = "hub"

    /**
     * Loads the hub spawn location from the location manager.
     * Should be called on plugin startup.
     */
    fun loadHubSpawn() {
        // The LocationManager will handle loading all locations
        if (isHubSpawnSet()) {
            Bukkit.getLogger().info("[Skylife] Hub spawn loaded successfully")
        } else {
            Bukkit.getLogger().warning("[Skylife] Hub spawn not set! Use /sethub to configure it.")
        }
    }

    /**
     * Sets the hub spawn location to the specified location.
     * @param location The new hub spawn location
     */
    fun setHubSpawn(location: Location) {
        LocationManager.saveLocation(HUB_LOCATION_NAME, location)
    }

    /**
     * Gets the current hub spawn location.
     * @return The hub spawn location, or null if not set
     */
    fun getHubSpawn(): Location? = LocationManager.getLocation(HUB_LOCATION_NAME)

    /**
     * Teleports a player to the hub spawn.
     * If no hub spawn is set, teleports to world spawn.
     * @param player The player to teleport
     */
    fun teleportToHub(player: Player) {
        val spawn = getHubSpawn()
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
    fun isHubSpawnSet(): Boolean = LocationManager.hasLocation(HUB_LOCATION_NAME)
}
