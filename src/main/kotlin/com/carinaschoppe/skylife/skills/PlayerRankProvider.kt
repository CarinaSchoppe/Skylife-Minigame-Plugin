package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.economy.PlayerRank
import org.bukkit.entity.Player

fun interface PlayerRankProvider {
    fun getRank(player: Player): PlayerRank
}