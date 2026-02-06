package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GamePattern

interface GameFactory {
    fun createFromPattern(pattern: GamePattern): Game
}

