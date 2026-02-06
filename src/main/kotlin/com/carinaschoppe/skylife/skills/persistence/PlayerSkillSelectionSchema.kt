package com.carinaschoppe.skylife.skills.persistence

import com.carinaschoppe.skylife.skills.Skill
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Database table for player skill selections.
 * Supports up to 4 skill slots (for VIP+ players and admins).
 */
object PlayerSkillsTable : IntIdTable("player_skills") {
    val playerUUID = varchar("player_uuid", 36).uniqueIndex()
    val skill1 = enumerationByName("skill1", 30, Skill::class).nullable()
    val skill2 = enumerationByName("skill2", 30, Skill::class).nullable()
    val skill3 = enumerationByName("skill3", 30, Skill::class).nullable()
    val skill4 = enumerationByName("skill4", 30, Skill::class).nullable()
}

/**
 * Entity class for player skills database operations.
 * Supports up to 4 skill slots.
 */
class PlayerSkillSelection(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, PlayerSkillSelection>(PlayerSkillsTable)

    var playerUUID by PlayerSkillsTable.playerUUID
    var skill1 by PlayerSkillsTable.skill1
    var skill2 by PlayerSkillsTable.skill2
    var skill3 by PlayerSkillsTable.skill3
    var skill4 by PlayerSkillsTable.skill4
}
