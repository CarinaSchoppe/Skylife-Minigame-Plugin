package com.carinaschoppe.skylife.game.services

import org.bukkit.entity.Player

fun interface StatsService {
    fun addStatsToPlayerWhenJoiningGame(player: Player)
}