package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

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

