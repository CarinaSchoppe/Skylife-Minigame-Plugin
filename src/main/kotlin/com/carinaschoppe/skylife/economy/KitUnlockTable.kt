package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 * Database table for tracking which kits players have unlocked.
 */
object KitUnlockTable : IntIdTable("kit_unlocks") {
    val playerUUID = varchar("player_uuid", 36)
    val kitName = varchar("kit_name", 64)

    init {
        uniqueIndex(playerUUID, kitName)
    }
}

/**
 * Entity class for KitUnlock.
 */
class KitUnlock(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, KitUnlock>(KitUnlockTable)

    var playerUUID by KitUnlockTable.playerUUID
    var kitName by KitUnlockTable.kitName
}
