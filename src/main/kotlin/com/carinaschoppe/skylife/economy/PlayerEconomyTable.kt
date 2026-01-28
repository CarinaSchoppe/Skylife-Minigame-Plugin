package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.IntEntity
import org.jetbrains.exposed.v1.core.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

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
class PlayerEconomy(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerEconomy>(PlayerEconomyTable)

    var playerUUID by PlayerEconomyTable.playerUUID
    var coins by PlayerEconomyTable.coins
}
