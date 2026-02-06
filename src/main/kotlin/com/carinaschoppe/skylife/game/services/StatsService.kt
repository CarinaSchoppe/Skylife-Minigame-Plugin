package com.carinaschoppe.skylife.game.services

import org.bukkit.entity.Player

interface StatsService {
    fun addStatsToPlayerWhenJoiningGame(player: Player)
}