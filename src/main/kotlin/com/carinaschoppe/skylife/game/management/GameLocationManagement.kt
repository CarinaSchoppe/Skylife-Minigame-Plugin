package com.carinaschoppe.skylife.game.management

import org.bukkit.Location

class GameLocationManagement {


    lateinit var lobbyLocation: Location

    lateinit var spectatorLocation: Location

    var spawnLocations = mutableSetOf<Location>()

    fun gamePatternComplete(): Boolean {
        return ::lobbyLocation.isInitialized and ::spectatorLocation.isInitialized and spawnLocations.isNotEmpty()
    }
}