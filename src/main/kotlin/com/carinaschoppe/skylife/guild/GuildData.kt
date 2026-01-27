package com.carinaschoppe.skylife.guild

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Database table for guilds.
 * Stores guild information including name, tag, leader, and friendly fire setting.
 */
object Guilds : IntIdTable() {
    val name = varchar("name", 24).uniqueIndex()
    val tag = varchar("tag", 5).uniqueIndex()
    val leaderUUID = varchar("leader_uuid", 36)
    val friendlyFireEnabled = bool("friendly_fire_enabled").default(false)
}

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

/**
 * Database table for guild members.
 * Stores the relationship between players and guilds, including their role.
 */
object GuildMembers : IntIdTable() {
    val playerUUID = varchar("player_uuid", 36).uniqueIndex()
    val guildId = reference("guild_id", Guilds)
    val role = enumerationByName("role", 10, GuildRole::class)
}

/**
 * Entity class for guild member database operations.
 */
class GuildMember(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, GuildMember>(GuildMembers)

    var playerUUID by GuildMembers.playerUUID
    var guildId by GuildMembers.guildId
    var role by GuildMembers.role
}

/**
 * Represents a player's role within a guild.
 */
enum class GuildRole {
    LEADER,
    ELDER,
    MEMBER
}
