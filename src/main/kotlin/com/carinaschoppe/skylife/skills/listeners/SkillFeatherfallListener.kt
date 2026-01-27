package com.carinaschoppe.skylife.skills.listeners

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Listener for the Featherfall skill.
 * Reduces fall damage by 50% for players with the skill active.
 */
class SkillFeatherfallListener : Listener {

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return

        val player = event.entity as? Player ?: return

        if (SkillsManager.hasSkillActive(player, Skill.FEATHERFALL)) {
            event.damage = event.damage * 0.5 // 50% reduction
        }
    }
}
