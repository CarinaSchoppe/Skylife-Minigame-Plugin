package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.PlayerPriority
import org.bukkit.entity.Player

interface PlayerPriorityResolver {
    fun getPlayerPriority(player: Player): PlayerPriority
    fun findPlayerToKick(players: List<Player>, joiningPlayer: Player): Player?
}