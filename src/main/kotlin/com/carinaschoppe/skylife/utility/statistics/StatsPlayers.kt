package com.carinaschoppe.skylife.utility.statistics

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable

object StatsPlayers : IdTable<String>("stats_players") {
    val uuid = varchar("uuid", 36)
    val kills = integer("kills")
    val deaths = integer("deaths")
    val wins = integer("wins")
    val games = integer("games")
    val name = varchar("name", 36)
    val points = integer("points")
    override val id: Column<EntityID<String>> = uuid.entityId()
    override val primaryKey = PrimaryKey(uuid, name = "UserUID")
}