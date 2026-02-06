package com.carinaschoppe.skylife.skills

import java.util.*

class DefaultSkillUnlockService : SkillUnlockService {
    override fun hasUnlocked(player: UUID, skill: Skill): Boolean {
        return SkillUnlockManager.hasUnlocked(player, skill)
    }
}