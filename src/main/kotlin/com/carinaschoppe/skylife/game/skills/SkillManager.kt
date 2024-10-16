package com.carinaschoppe.skylife.game.skills

import org.bukkit.entity.Player

object SkillManager {

    val playerSkills = mutableMapOf<Player, MutableSet<Class<out Skill>>>()

    val skills = mapOf<Class<out Skill>, Skill>(
        SlowFallBootsSkill::class.java to SlowFallBootsSkill(),
    )

}