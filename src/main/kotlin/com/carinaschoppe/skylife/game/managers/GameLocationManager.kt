package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Manages all game-related locations for a specific game instance or pattern.
 *
 * This class is responsible for storing and providing access to various important
 * locations used throughout the game, such as lobby spawns, spectator areas, and
 * player spawn points. It also provides utility methods for location conversion.
 *
 * @property lobbyLocation The location where players spawn when joining the game lobby
 * @property spectatorLocation The location where spectators are teleported
 * @property mainLocation The primary game location (e.g., map center or main area)
 * @property spawnLocations Set of available spawn locations for players during the game
 */
class GameLocationManager {

    /**
     * The location where players spawn when joining the game lobby.
     * This should be set before the game starts.
     */
    lateinit var lobbyLocation: SkylifeLocation

    /**
     * The location where spectators are teleported when entering spectator mode.
     */
    lateinit var spectatorLocation: SkylifeLocation

    /**
     * The primary location for the game, often used as a reference point.
     * This could be the center of the map or another significant location.
     */
    lateinit var mainLocation: SkylifeLocation

    /**
     * A set of available spawn locations for players during the game.
     * These are typically distributed around the game area to prevent spawn camping.
     */
    val spawnLocations = mutableSetOf<SkylifeLocation>()

    /**
     * Checks if a specific location has been initialized.
     *
     * @param locationType The type of location to check ("lobby", "spectator", or "main")
     * @return `true` if the location is initialized, `false` otherwise
     */
    fun isLocationInitialized(locationType: String): Boolean {
        return when (locationType.lowercase()) {
            "lobby" -> ::lobbyLocation.isInitialized
            "spectator" -> ::spectatorLocation.isInitialized
            "main" -> ::mainLocation.isInitialized
            else -> false
        }
    }

    /**
     * Checks if all required locations for a game pattern have been set.
     *
     * @return `true` if all required locations are initialized and at least one spawn location exists,
     *         `false` otherwise
     */
    fun gamePatternComplete(): Boolean {
        return ::lobbyLocation.isInitialized &&
                ::spectatorLocation.isInitialized &&
                spawnLocations.isNotEmpty() &&
                ::mainLocation.isInitialized
    }

    companion object {
        /**
         * Converts a [SkylifeLocation] to a Bukkit [Location].
         *
         * @param skylifeLocation The [SkylifeLocation] to convert
         * @return The corresponding Bukkit [Location] object, or null if world doesn't exist
         */
        fun skylifeLocationToLocationConverter(skylifeLocation: SkylifeLocation): Location? {
            val world = Bukkit.getWorld(skylifeLocation.world)

            if (world == null) {
                Bukkit.getLogger().warning("[GameLocationManager] World '${skylifeLocation.world}' is not loaded or doesn't exist")
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
         * Converts a Bukkit [Location] to a [SkylifeLocation].
         *
         * @param location The Bukkit [Location] to convert
         * @return A new [SkylifeLocation] representing the same position and orientation
         */
        fun locationToSkylifeLocationConverter(location: Location): SkylifeLocation {
            return SkylifeLocation(
                location.world?.name ?: "world",
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch
            )
        }
    }
}