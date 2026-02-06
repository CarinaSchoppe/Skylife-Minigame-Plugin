package com.carinaschoppe.skylife.skills

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

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


