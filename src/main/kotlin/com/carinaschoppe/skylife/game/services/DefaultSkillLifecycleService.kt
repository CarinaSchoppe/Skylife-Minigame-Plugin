package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.skills.SkillEffectsManager
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.entity.Player

class DefaultSkillLifecycleService : SkillLifecycleService {
    override fun deactivateSkills(player: Player) {
        SkillsManager.deactivateSkills(player)
    }

    override fun removeSkillEffects(player: Player) {
        SkillEffectsManager.removeSkillEffects(player)
    }
}