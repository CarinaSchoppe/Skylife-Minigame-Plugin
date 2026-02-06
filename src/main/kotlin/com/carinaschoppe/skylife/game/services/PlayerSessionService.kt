package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import org.bukkit.entity.Player

interface PlayerSessionService {
    fun prepareForGameJoin(player: Player, game: Game)
    fun attachGameScoreboard(player: Player, game: Game)
    fun updateGameScoreboards(game: Game)
    fun resetToHubFromGame(player: Player)
    fun resetToHubAfterGameStop(player: Player)
}

