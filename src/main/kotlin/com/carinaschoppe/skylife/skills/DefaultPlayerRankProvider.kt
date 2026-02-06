package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.economy.PlayerRank
import org.bukkit.entity.Player

class DefaultPlayerRankProvider : PlayerRankProvider {
    override fun getRank(player: Player): PlayerRank = PlayerRank.getRank(player)
}