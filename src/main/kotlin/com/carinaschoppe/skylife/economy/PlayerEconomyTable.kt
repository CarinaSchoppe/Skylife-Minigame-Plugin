package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Database table definition for player economy data.
 * Stores each player's coin balance with a unique index on player UUID.
 *
 * Table schema:
 * - id: Auto-incrementing primary key
 * - player_uuid: Unique 36-character UUID string (indexed)
 * - coins: Integer coin balance (defaults to 0)
 */
object PlayerEconomyTable : IntIdTable("player_economy") {
    val playerUUID = varchar("player_uuid", 36).uniqueIndex()
    val coins = integer("coins").default(0)
}

/**
 * Entity class for accessing and manipulating player economy records.
 * Represents a single row in the player_economy table.
 */
class PlayerEconomy(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, PlayerEconomy>(PlayerEconomyTable)

    /** The player's UUID as a string */
    var playerUUID by PlayerEconomyTable.playerUUID

    /** The player's current coin balance */
    var coins by PlayerEconomyTable.coins
}
