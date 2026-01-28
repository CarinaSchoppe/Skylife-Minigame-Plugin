package com.carinaschoppe.skylife.economy

import org.jetbrains.exposed.v1.core.dao.IntEntity
import org.jetbrains.exposed.v1.core.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

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
class KitUnlock(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<KitUnlock>(KitUnlockTable)

    var playerUUID by KitUnlockTable.playerUUID
    var kitName by KitUnlockTable.kitName
}
