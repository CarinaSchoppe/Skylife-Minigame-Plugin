package com.carinaschoppe.skylife.skills.persistence

import com.carinaschoppe.skylife.skills.Skill
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

/**
 * Database table for player skill selections.
 * Supports up to 4 skill slots (for VIP+ players and admins).
 */
object PlayerSkillsSchema : IntIdTable("player_skills") {
    val playerUUID = varchar("player_uuid", 36).uniqueIndex()
    val skill1 = enumerationByName("skill1", 30, Skill::class).nullable()
    val skill2 = enumerationByName("skill2", 30, Skill::class).nullable()
    val skill3 = enumerationByName("skill3", 30, Skill::class).nullable()
    val skill4 = enumerationByName("skill4", 30, Skill::class).nullable()
}

