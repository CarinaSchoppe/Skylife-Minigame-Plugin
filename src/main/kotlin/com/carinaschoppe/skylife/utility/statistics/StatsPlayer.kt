package com.carinaschoppe.skylife.utility.statistics

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass


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