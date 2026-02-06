package com.carinaschoppe.skylife.skills.persistence

import com.carinaschoppe.skylife.skills.Skill
import java.util.*

interface PlayerSkillSelectionRepository {
    fun loadAllSelections(): Map<UUID, Set<Skill>>
    fun saveSelection(uuid: UUID, skills: Set<Skill>)
    fun deleteSelection(uuid: UUID)
}

