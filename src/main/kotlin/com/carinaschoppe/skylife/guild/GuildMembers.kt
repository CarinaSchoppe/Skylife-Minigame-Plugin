package com.carinaschoppe.skylife.guild

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

/**
 * Database table for guild members.
 * Stores the relationship between players and guilds, including their role.
 */
object GuildMembers : IntIdTable("guild_members") {
    val playerUUID = varchar("player_uuid", 36).uniqueIndex()
    val guildId = reference("guild_id", Guilds)
    val role = enumerationByName("role", 6, GuildRole::class)
}