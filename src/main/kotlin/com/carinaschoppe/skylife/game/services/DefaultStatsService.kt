package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.entity.Player

class DefaultStatsService : StatsService {
    override fun addStatsToPlayerWhenJoiningGame(player: Player) {
        StatsUtility.addStatsToPlayerWhenJoiningGame(player)
    }
}