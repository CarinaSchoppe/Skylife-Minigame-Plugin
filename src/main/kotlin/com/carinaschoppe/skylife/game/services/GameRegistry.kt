package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GamePattern

interface GameRegistry {
    val lobbyGames: MutableList<Game>
    val activeGames: MutableList<Game>
    val gamePatterns: MutableList<GamePattern>
}

class InMemoryGameRegistry : GameRegistry {
    override val lobbyGames = mutableListOf<Game>()
    override val activeGames = mutableListOf<Game>()
    override val gamePatterns = mutableListOf<GamePattern>()
}
