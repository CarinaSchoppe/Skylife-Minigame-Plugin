package com.carinaschoppe.skylife.guild

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Entity class for guild member database operations.
 */
class GuildMember(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, GuildMember>(GuildMembers)

    var playerUUID by GuildMembers.playerUUID
    var guildId by GuildMembers.guildId
    var role by GuildMembers.role
}