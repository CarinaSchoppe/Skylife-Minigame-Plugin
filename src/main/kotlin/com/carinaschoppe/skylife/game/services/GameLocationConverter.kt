package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import org.bukkit.Location

fun interface GameLocationConverter {
    fun toLocation(skylifeLocation: SkylifeLocation): Location?
}