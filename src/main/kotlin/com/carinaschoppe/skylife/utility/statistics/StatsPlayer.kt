package com.carinaschoppe.skylife.utility.statistics

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass


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

class StatsPlayer(uuid: EntityID<String>) : Entity<String>(uuid) {
    companion object : EntityClass<String, StatsPlayer>(StatsPlayers)

    var uuid by StatsPlayers.uuid
    var kills by StatsPlayers.kills
    var deaths by StatsPlayers.deaths
    var wins by StatsPlayers.wins
    var games by StatsPlayers.games
    var name by StatsPlayers.name
    var points by StatsPlayers.points

}