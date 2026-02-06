package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import org.bukkit.Location

class DefaultGameLocationConverter : GameLocationConverter {
    override fun toLocation(skylifeLocation: SkylifeLocation): Location? {
        return GameLocationManager.skylifeLocationToLocationConverter(skylifeLocation)
    }
}