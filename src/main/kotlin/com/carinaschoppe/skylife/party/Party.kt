package com.carinaschoppe.skylife.party

import java.util.*

/**
 * Represents a party of players who can play together.
 *
 * @property id Unique identifier for this party
 * @property leader UUID of the party leader
 * @property members Set of all party member UUIDs (including leader)
 */
data class Party(
    val id: UUID = UUID.randomUUID(),
    var leader: UUID,
    val members: MutableSet<UUID> = mutableSetOf()
) {
    /**
     * Checks if a player is the leader of this party.
     */
    fun isLeader(uuid: UUID): Boolean = leader == uuid

    /**
     * Checks if a player is a member of this party.
     */
    fun isMember(uuid: UUID): Boolean = members.contains(uuid)

    /**
     * Gets the size of the party.
     */
    fun size(): Int = members.size

    /**
     * Adds a member to the party.
     */
    fun addMember(uuid: UUID) {
        members.add(uuid)
    }

    /**
     * Removes a member from the party.
     */
    fun removeMember(uuid: UUID) {
        members.remove(uuid)
    }
}
