package com.carinaschoppe.skylife.game.management

import com.carinaschoppe.skylife.utility.miscellaneous.SkylifeLocation
import org.bukkit.Bukkit
import org.bukkit.Location

class GameLocationManagement {


    lateinit var lobbyLocation: SkylifeLocation


    lateinit var spectatorLocation: SkylifeLocation

    val spawnLocations = mutableSetOf<SkylifeLocation>()

    fun gamePatternComplete(): Boolean {
        return ::lobbyLocation.isInitialized and ::spectatorLocation.isInitialized and spawnLocations.isNotEmpty()
    }


    companion object {

        fun skylifeLocationToLocationConverter(skylifeLocation: SkylifeLocation): Location {
            return Location(Bukkit.getWorld(skylifeLocation.world), skylifeLocation.x, skylifeLocation.y, skylifeLocation.z, skylifeLocation.yaw, skylifeLocation.pitch)
        }

        fun locationToSkylifeLocationConverter(location: Location): SkylifeLocation {
            return SkylifeLocation(location.world.name, location.x, location.y, location.z, location.yaw, location.pitch)
        }
    }
}