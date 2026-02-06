package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.managers.MapManager
import org.bukkit.World
import java.util.*

class DefaultGameWorldLoader : GameWorldLoader {
    override fun loadWorldForGame(gameId: UUID, templateMapName: String?): World? {
        return MapManager.loadMapForGame(gameId, templateMapName)
    }
}