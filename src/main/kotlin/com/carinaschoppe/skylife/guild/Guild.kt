package com.carinaschoppe.skylife.guild

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Entity class for guild database operations.
 */
class Guild(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Guild>(Guilds)

    var name by Guilds.name
    var tag by Guilds.tag
    var leaderUUID by Guilds.leaderUUID
    var friendlyFireEnabled by Guilds.friendlyFireEnabled
}