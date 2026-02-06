package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.miscellaneous.PlayerPriority
import org.bukkit.entity.Player

class DefaultPlayerPriorityResolver : PlayerPriorityResolver {
    override fun getPlayerPriority(player: Player): PlayerPriority {
        return PlayerPriority.getPlayerPriority(player)
    }

    override fun findPlayerToKick(players: List<Player>, joiningPlayer: Player): Player? {
        return PlayerPriority.findPlayerToKick(players, joiningPlayer)
    }
}