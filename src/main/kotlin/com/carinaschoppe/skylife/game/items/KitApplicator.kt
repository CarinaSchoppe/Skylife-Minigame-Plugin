package com.carinaschoppe.skylife.game.items

import org.bukkit.entity.Player

interface KitApplicator {

    fun applyKit(player: Player, kit: Kit)
}