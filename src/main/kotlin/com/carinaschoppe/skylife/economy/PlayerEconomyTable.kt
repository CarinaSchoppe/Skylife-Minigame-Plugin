package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Database table for player economy (coins).
 */
object PlayerEconomyTable : IntIdTable("player_economy") {
    val playerUUID = varchar("player_uuid", 36).uniqueIndex()
    val coins = integer("coins").default(0)
}

/**
 * Entity class for PlayerEconomy.
 */
class PlayerEconomy(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, PlayerEconomy>(PlayerEconomyTable)

    var playerUUID by PlayerEconomyTable.playerUUID
    var coins by PlayerEconomyTable.coins
}
