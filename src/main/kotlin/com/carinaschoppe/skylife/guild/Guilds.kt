package com.carinaschoppe.skylife.guild

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

/**
 * Database table for guilds.
 * Stores guild information including name, tag, leader, and friendly fire setting.
 */
object Guilds : IntIdTable("guilds") {
    val name = varchar("name", 24).uniqueIndex()
    val tag = varchar("tag", 5).uniqueIndex()
    val leaderUUID = varchar("leader_uuid", 36)
    val friendlyFireEnabled = bool("friendly_fire_enabled").default(false)
}