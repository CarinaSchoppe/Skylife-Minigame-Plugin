package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.economy.PlayerRank
import org.bukkit.entity.Player

interface PlayerRankProvider {
    fun getRank(player: Player): PlayerRank
}