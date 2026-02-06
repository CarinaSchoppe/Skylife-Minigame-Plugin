package com.carinaschoppe.skylife.skills

import java.util.*

interface SkillUnlockService {
    fun hasUnlocked(player: UUID, skill: Skill): Boolean
}