package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.gamestates.IngameState
import org.bukkit.entity.Player

class DefaultGameStateFactory : GameStateFactory {
    override fun createIngameState(game: Game): GameState<Player> {
        return IngameState(game)
    }
}