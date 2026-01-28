package com.carinaschoppe.skylife.skills

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Database table definition for skill unlock tracking.
 * Stores which skills each player has unlocked, with a unique constraint per player-skill pair.
 *
 * Table schema:
 * - id: Auto-incrementing primary key
 * - player_uuid: 36-character UUID string
 * - skill_name: Skill enum name string
 * - Unique constraint: (player_uuid, skill_name) combination must be unique
 */
object SkillUnlockTable : IntIdTable("skill_unlocks") {
    val playerUUID = varchar("player_uuid", 36)
    val skillName = varchar("skill_name", 64)

    init {
        uniqueIndex(playerUUID, skillName)
    }
}

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
