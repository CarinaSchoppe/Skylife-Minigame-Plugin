package com.carinaschoppe.skylife.skills

import java.util.*

fun interface SkillUnlockService {
    fun hasUnlocked(player: UUID, skill: Skill): Boolean
}