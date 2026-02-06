package com.carinaschoppe.skylife.game.services

import org.bukkit.entity.Player

interface SkillLifecycleService {
    fun deactivateSkills(player: Player)
    fun removeSkillEffects(player: Player)
}