package com.carinaschoppe.skylife.skills.persistence

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Entity class for player skills database operations.
 * Supports up to 4 skill slots.
 */
class PlayerSkillSelection(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, PlayerSkillSelection>(PlayerSkillsSchema)

    var playerUUID by PlayerSkillsSchema.playerUUID
    var skill1 by PlayerSkillsSchema.skill1
    var skill2 by PlayerSkillsSchema.skill2
    var skill3 by PlayerSkillsSchema.skill3
    var skill4 by PlayerSkillsSchema.skill4
}