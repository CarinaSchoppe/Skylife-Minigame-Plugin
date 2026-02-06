package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.gamestates.GameState
import org.bukkit.entity.Player

interface GameStateFactory {
    fun createIngameState(game: Game): GameState<Player>
}