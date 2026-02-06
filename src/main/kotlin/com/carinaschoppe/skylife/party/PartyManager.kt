package com.carinaschoppe.skylife.party

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.platform.PluginContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages all party operations including creation, invites, and membership.
 */
object PartyManager {

    private const val ERROR_PARTY_NOT_FOUND = "Party not found"
    private const val ERROR_TARGET_NOT_IN_PARTY = "Target is not in the party"

    // Cache: Player UUID -> Party
    private val playerPartyCache = ConcurrentHashMap<UUID, Party>()

    // Cache: Party ID -> Party
    private val parties = ConcurrentHashMap<UUID, Party>()

    // Cache: Invitee UUID -> List of pending invites
    private val pendingInvites = ConcurrentHashMap<UUID, MutableList<PartyInvite>>()

    /**
     * Creates a new party with the given leader.
     */
    fun createParty(leader: UUID): Party {
        val party = Party(leader = leader)
        party.addMember(leader)
        parties[party.id] = party
        playerPartyCache[leader] = party
        return party
    }

    /**
     * Gets the party a player is in.
     */
    fun getPlayerParty(uuid: UUID): Party? {
        return playerPartyCache[uuid]
    }

    /**
     * Gets a party by its ID.
     */
    fun getParty(partyId: UUID): Party? {
        return parties[partyId]
    }

    /**
     * Checks if a player is in a party.
     */
    fun isInParty(uuid: UUID): Boolean {
        return playerPartyCache.containsKey(uuid)
    }

    /**
     * Invites a player to a party.
     * @return Result with success or error message
     */
    fun invitePlayer(partyId: UUID, inviter: UUID, invitee: UUID): Result<Unit> {
        val party = parties[partyId] ?: return Result.failure(Exception(ERROR_PARTY_NOT_FOUND))

        // Check if inviter is the leader
        if (!party.isLeader(inviter)) {
            return Result.failure(Exception("Only the party leader can invite players"))
        }

        // Check if invitee is online
        val inviteePlayer = Bukkit.getPlayer(invitee)
            ?: return Result.failure(Exception("Player must be online to be invited"))

        // Check if invitee is already in a party
        if (isInParty(invitee)) {
            return Result.failure(Exception("${inviteePlayer.name} is already in a party"))
        }

        // Check if invitee already has a pending invite from this party
        val existingInvites = pendingInvites.getOrPut(invitee) { mutableListOf() }

        // Clean up expired invites
        existingInvites.removeIf { it.isExpired() }

        if (existingInvites.any { it.partyId == partyId }) {
            return Result.failure(Exception("${inviteePlayer.name} already has a pending invite from your party"))
        }

        // Create invite
        val invite = PartyInvite(partyId, inviter, invitee)
        existingInvites.add(invite)

        // Schedule invite expiration
        Bukkit.getScheduler().runTaskLater(PluginContext.plugin, Runnable {
            pendingInvites[invitee]?.remove(invite)
        }, PartyInvite.INVITE_TIMEOUT_MS / 50) // Convert ms to ticks

        return Result.success(Unit)
    }

    /**
     * Accepts a party invite.
     * @return Result with success or error message
     */
    fun acceptInvite(invitee: UUID, inviterName: String): Result<Party> {
        // Check if player is already in a party
        if (isInParty(invitee)) {
            return Result.failure(Exception("You are already in a party. Leave your current party first."))
        }

        val invites = pendingInvites[invitee] ?: return Result.failure(Exception("You have no pending party invites"))

        // Clean up expired invites
        invites.removeIf { it.isExpired() }

        // Find invite from the specified inviter
        val inviterPlayer = Bukkit.getPlayerExact(inviterName)
        val inviterUUID = inviterPlayer?.uniqueId ?: Bukkit.getOfflinePlayer(inviterName).uniqueId

        val invite = invites.firstOrNull { it.inviter == inviterUUID }
            ?: return Result.failure(Exception("No pending invite from $inviterName found or invite has expired"))

        val party = parties[invite.partyId]
            ?: return Result.failure(Exception("Party no longer exists"))

        // Add player to party
        party.addMember(invitee)
        playerPartyCache[invitee] = party

        // Remove all invites for this player
        pendingInvites.remove(invitee)

        return Result.success(party)
    }

    /**
     * Removes a player from their party.
     */
    fun leaveParty(uuid: UUID): Result<Unit> {
        val party = playerPartyCache[uuid]
            ?: return Result.failure(Exception("You are not in a party"))

        // Handle leadership transfer or party deletion BEFORE removing member
        if (party.isLeader(uuid) && party.size() > 1) {
            // Transfer leadership to next member (excluding the leaving player)
            val newLeader = party.members.first { it != uuid }
            party.leader = newLeader
        }

        party.removeMember(uuid)
        playerPartyCache.remove(uuid)

        // If player was in a game, remove them
        val player = Bukkit.getPlayer(uuid)
        if (player != null && GameCluster.getGame(player) != null) {
            GameCluster.removePlayerFromGame(player)
        }

        // Delete empty party after removing the member
        if (party.size() == 0) {
            deleteParty(party.id)
        }

        return Result.success(Unit)
    }

    /**
     * Promotes a member to party leader.
     */
    fun promoteToLeader(partyId: UUID, promoter: UUID, target: UUID): Result<Unit> {
        val party = parties[partyId] ?: return Result.failure(Exception(ERROR_PARTY_NOT_FOUND))

        if (!party.isLeader(promoter)) {
            return Result.failure(Exception("Only the party leader can promote members"))
        }

        if (!party.isMember(target)) {
            return Result.failure(Exception(ERROR_TARGET_NOT_IN_PARTY))
        }

        if (party.isLeader(target)) {
            return Result.failure(Exception("Target is already the party leader"))
        }

        party.leader = target
        return Result.success(Unit)
    }

    /**
     * Kicks a player from the party (leader only).
     */
    fun kickPlayer(partyId: UUID, kicker: UUID, target: UUID): Result<Unit> {
        val party = parties[partyId] ?: return Result.failure(Exception(ERROR_PARTY_NOT_FOUND))

        if (!party.isLeader(kicker)) {
            return Result.failure(Exception("Only the party leader can kick members"))
        }

        if (!party.isMember(target)) {
            return Result.failure(Exception(ERROR_TARGET_NOT_IN_PARTY))
        }

        if (party.isLeader(target)) {
            return Result.failure(Exception("Cannot kick the party leader"))
        }

        party.removeMember(target)
        playerPartyCache.remove(target)

        // If kicked player was in a game, remove them
        val player = Bukkit.getPlayer(target)
        if (player != null && GameCluster.getGame(player) != null) {
            GameCluster.removePlayerFromGame(player)
        }

        // Check if party is now empty
        if (party.size() == 0) {
            deleteParty(party.id)
        }

        return Result.success(Unit)
    }

    /**
     * Deletes a party completely.
     */
    private fun deleteParty(partyId: UUID) {
        val party = parties[partyId] ?: return

        party.members.forEach { memberUUID ->
            playerPartyCache.remove(memberUUID)
        }

        parties.remove(partyId)
    }

    /**
     * Gets all online members of a party.
     */
    fun getOnlinePartyMembers(party: Party): List<Player> {
        return party.members.mapNotNull { Bukkit.getPlayer(it) }
    }

    /**
     * Gets all pending invites for a player.
     */
    fun getPendingInvites(uuid: UUID): List<PartyInvite> {
        val invites = pendingInvites[uuid] ?: return emptyList()
        invites.removeIf { it.isExpired() }
        return invites.toList()
    }

    /**
     * Handles party member joining a game.
     * If leader joins, all online party members join too.
     * If the target game is full, tries to find another available game.
     */
    fun handlePartyGameJoin(player: Player, targetGame: com.carinaschoppe.skylife.game.Game?, mapName: String? = null): Result<Unit> {
        val party = getPlayerParty(player.uniqueId) ?: return Result.success(Unit) // Not in party, normal join

        // If not leader, only they join
        if (!party.isLeader(player.uniqueId)) {
            return Result.success(Unit)
        }

        // Leader is joining - bring all online party members
        val onlineMembers = getOnlinePartyMembers(party)
        val gameToJoin = resolvePartyGame(targetGame, mapName, onlineMembers.size).getOrElse { error ->
            return Result.failure(error)
        }

        removeMembersFromGames(onlineMembers)

        val failedJoins = addMembersToGame(onlineMembers, gameToJoin)
        if (failedJoins.isNotEmpty()) {
            removeMembersFromGames(onlineMembers)
            return Result.failure(Exception("Failed to add all party members to game. ${failedJoins.size} players could not join."))
        }

        return Result.success(Unit)
    }

    /**
     * Checks if two players are in the same party.
     */
    fun areInSameParty(player1: UUID, player2: UUID): Boolean {
        val party1 = playerPartyCache[player1] ?: return false
        val party2 = playerPartyCache[player2] ?: return false
        return party1.id == party2.id
    }

    /**
     * Cleans up party data when a player disconnects.
     */
    fun handlePlayerDisconnect(uuid: UUID) {
        // Remove pending invites for this player
        pendingInvites.remove(uuid)

        // Remove from their party
        val party = playerPartyCache[uuid]
        if (party != null) {
            leaveParty(uuid)
        }
    }

    /**
     * Gets the party leader of a party.
     */
    fun getPartyLeader(party: Party): Player? {
        return Bukkit.getPlayer(party.leader)
    }

    private fun resolvePartyGame(
        targetGame: com.carinaschoppe.skylife.game.Game?,
        mapName: String?,
        partySize: Int
    ): Result<com.carinaschoppe.skylife.game.Game> {
        if (targetGame == null) {
            return Result.failure(Exception("No game specified"))
        }

        val availableSlots = targetGame.maxPlayers - targetGame.livingPlayers.size
        if (availableSlots >= partySize) {
            return Result.success(targetGame)
        }

        if (mapName != null) {
            val alternative = GameCluster.lobbyGamesList
                .filter { it.pattern.mapName == mapName }
                .firstOrNull { (it.maxPlayers - it.livingPlayers.size) >= partySize }

            if (alternative != null) {
                return Result.success(alternative)
            }
        }

        return Result.failure(Exception("No available game found with enough space for all party members ($partySize players needed)"))
    }

    private fun removeMembersFromGames(members: List<Player>) {
        members.forEach { member ->
            if (GameCluster.getGame(member) != null) {
                GameCluster.removePlayerFromGame(member)
            }
        }
    }

    private fun addMembersToGame(
        members: List<Player>,
        game: com.carinaschoppe.skylife.game.Game
    ): List<Player> {
        val failed = mutableListOf<Player>()
        members.forEach { member ->
            if (!GameCluster.addPlayerToGame(member, game)) {
                failed.add(member)
            }
        }
        return failed
    }
}
