package com.carinaschoppe.skylife.game.services

import org.bukkit.World
import java.util.*

interface GameWorldLoader {
    fun loadWorldForGame(gameId: UUID, templateMapName: String? = null): World?
}