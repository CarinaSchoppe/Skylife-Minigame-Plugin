package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Database table definition for kit unlock tracking.
 * Stores which kits each player has unlocked, with a unique constraint per player-kit pair.
 *
 * Table schema:
 * - id: Auto-incrementing primary key
 * - player_uuid: 36-character UUID string
 * - kit_name: 64-character kit name string
 * - Unique constraint: (player_uuid, kit_name) combination must be unique
 */
object KitUnlockTable : IntIdTable("kit_unlocks") {
    val playerUUID = varchar("player_uuid", 36)
    val kitName = varchar("kit_name", 64)

    init {
        uniqueIndex(playerUUID, kitName)
    }
}

/**
 * Entity class for accessing and manipulating kit unlock records.
 * Represents a single row in the kit_unlocks table.
 */
class KitUnlock(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, KitUnlock>(KitUnlockTable)

    /** The player's UUID as a string */
    var playerUUID by KitUnlockTable.playerUUID

    /** The name of the unlocked kit */
    var kitName by KitUnlockTable.kitName
}
