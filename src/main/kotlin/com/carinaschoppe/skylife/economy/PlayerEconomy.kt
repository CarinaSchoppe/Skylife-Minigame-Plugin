package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

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