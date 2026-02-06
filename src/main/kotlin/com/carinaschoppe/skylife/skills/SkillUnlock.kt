package com.carinaschoppe.skylife.skills

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Entity class for accessing and manipulating skill unlock records.
 * Represents a single row in the skill_unlocks table.
 */
class SkillUnlock(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, SkillUnlock>(SkillUnlockTable)

    /** The player's UUID as a string */
    var playerUUID by SkillUnlockTable.playerUUID

    /** The name of the unlocked skill */
    var skillName by SkillUnlockTable.skillName
}