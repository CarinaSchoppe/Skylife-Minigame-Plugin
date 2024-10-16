package com.carinaschoppe.skylife.events.skills

import com.carinaschoppe.skylife.game.skills.SkillManager
import com.carinaschoppe.skylife.game.skills.SlowFallBootsSkill
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class SlowFallBootsSkillListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onFallDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (!(SkillManager.playerSkills.contains(player))) return
        if (!(SkillManager.playerSkills[player]!!.contains(SlowFallBootsSkill::class.java))) return

        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.damage = 0.0
            event.isCancelled = true
        }

    }
}