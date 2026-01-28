package com.carinaschoppe.skylife.utility.location

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File

/**
 * Manages named locations for the plugin.
 * Locations are stored in JSON files in the locations/ folder.
 */
object LocationManager {

    private val locationsFolder = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "locations/")
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val locations = mutableMapOf<String, SkylifeLocation>()

    /**
     * Loads all locations from the locations folder.
     */
    fun loadLocations() {
        if (!locationsFolder.exists()) {
            locationsFolder.mkdirs()
        }

        locationsFolder.listFiles { file -> file.extension == "json" }?.forEach { file ->
            try {
                val json = file.readText()
                val location = gson.fromJson(json, SkylifeLocation::class.java)
                val name = file.nameWithoutExtension
                locations[name] = location
                Bukkit.getLogger().info("[Skylife] Loaded location: $name")
            } catch (e: Exception) {
                Bukkit.getLogger().warning("[Skylife] Failed to load location from ${file.name}: ${e.message}")
            }
        }
    }

    /**
     * Saves a location to a JSON file.
     * @param name The name of the location
     * @param location The location to save
     */
    fun saveLocation(name: String, location: Location) {
        val skylifeLocation = SkylifeLocation(
            location.world?.name ?: "world",
            location.x,
            location.y,
            location.z,
            location.yaw,
            location.pitch
        )

        locations[name] = skylifeLocation

        val file = File(locationsFolder, "$name.json")
        val json = gson.toJson(skylifeLocation)
        file.writeText(json)
    }

    /**
     * Gets a location by name.
     * @param name The name of the location
     * @return The location, or null if not found
     */
    fun getLocation(name: String): Location? {
        val skylifeLocation = locations[name] ?: return null

        val world = Bukkit.getWorld(skylifeLocation.world) ?: run {
            Bukkit.getLogger().warning("[Skylife] World '${skylifeLocation.world}' not found for location '$name'")
            return null
        }

        return Location(
            world,
            skylifeLocation.x,
            skylifeLocation.y,
            skylifeLocation.z,
            skylifeLocation.yaw,
            skylifeLocation.pitch
        )
    }

    /**
     * Checks if a location exists.
     * @param name The name of the location
     * @return true if the location exists, false otherwise
     */
    fun hasLocation(name: String): Boolean = locations.containsKey(name)

    /**
     * Deletes a location.
     * @param name The name of the location
     */
    fun deleteLocation(name: String) {
        locations.remove(name)
        val file = File(locationsFolder, "$name.json")
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Gets all location names.
     * @return A set of all location names
     */
    fun getAllLocationNames(): Set<String> = locations.keys.toSet()
}
