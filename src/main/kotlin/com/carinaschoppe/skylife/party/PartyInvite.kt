package com.carinaschoppe.skylife.party

import java.util.*

/**
 * Represents a pending party invitation.
 *
 * @property partyId The ID of the party
 * @property inviter UUID of the player who sent the invite
 * @property invitee UUID of the player who received the invite
 * @property timestamp When the invite was sent (in milliseconds)
 */
data class PartyInvite(
    val partyId: UUID,
    val inviter: UUID,
    val invitee: UUID,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val INVITE_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes
    }

    /**
     * Checks if this invite has expired.
     */
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - timestamp > INVITE_TIMEOUT_MS
    }

    /**
     * Gets remaining time in seconds before this invite expires.
     */
    fun getRemainingSeconds(): Long {
        val elapsed = System.currentTimeMillis() - timestamp
        val remaining = INVITE_TIMEOUT_MS - elapsed
        return (remaining / 1000).coerceAtLeast(0)
    }
}
