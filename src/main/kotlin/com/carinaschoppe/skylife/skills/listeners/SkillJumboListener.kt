package com.carinaschoppe.skylife.skills.listeners

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

/**
 * Listener for the Jumbo skill.
 * Prevents hunger loss for players with the skill active.
 */
class SkillJumboListener : Listener {

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return

        if (SkillsManager.hasSkillActive(player, Skill.JUMBO)) {
            event.isCancelled = true
            player.foodLevel = 20
            player.saturation = 20f
        }
    }
}
