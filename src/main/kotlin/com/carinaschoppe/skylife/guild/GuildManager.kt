package com.carinaschoppe.skylife.guild

import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages all guild operations including creation, membership, and settings.
 * Uses in-memory cache for performance with database persistence.
 */
object GuildManager {

    private const val ERROR_GUILD_NOT_FOUND = "Guild not found"
    private const val ERROR_NOT_IN_GUILD = "You are not in this guild"

    // Cache: Player UUID -> Guild ID
    private val playerGuildCache = ConcurrentHashMap<UUID, Int>()

    // Cache: Guild ID -> GuildInfo
    private val guildCache = ConcurrentHashMap<Int, GuildInfo>()

    // Cache: Guild Name (lowercase) -> Guild ID
    private val guildNameIndex = ConcurrentHashMap<String, Int>()

    // Cache: Guild Tag (lowercase) -> Guild ID
    private val guildTagIndex = ConcurrentHashMap<String, Int>()

    /**
     * In-memory representation of a guild with all its members.
     */


    /**
     * Loads all guilds from database into cache.
     * Should be called on plugin startup.
     */
    fun loadGuilds() {
        transaction {
            Guild.all().forEach { guild ->
                val members = mutableMapOf<UUID, GuildRole>()
                GuildMember.find { GuildMembers.guildId eq guild.id }.forEach { member ->
                    members[UUID.fromString(member.playerUUID)] = member.role
                }

                val guildInfo = GuildInfo(
                    id = guild.id.value,
                    name = guild.name,
                    tag = guild.tag,
                    leaderUUID = UUID.fromString(guild.leaderUUID),
                    members = members,
                    friendlyFireEnabled = guild.friendlyFireEnabled
                )

                guildCache[guild.id.value] = guildInfo
                guildNameIndex[guild.name.lowercase()] = guild.id.value
                guildTagIndex[guild.tag.lowercase()] = guild.id.value

                members.keys.forEach { uuid ->
                    playerGuildCache[uuid] = guild.id.value
                }
            }
        }
    }

    /**
     * Creates a new guild with the specified name and tag.
     * @return Result with guild ID on success, or error message on failure.
     */
    fun createGuild(name: String, tag: String, leader: UUID): Result<Int> {
        if (name.length > 24) {
            return Result.failure(Exception("Guild name must be 24 characters or less"))
        }
        if (tag.length > 5) {
            return Result.failure(Exception("Guild tag must be 5 characters or less"))
        }
        if (playerGuildCache.containsKey(leader)) {
            return Result.failure(Exception("You are already in a guild"))
        }
        if (guildNameIndex.containsKey(name.lowercase())) {
            return Result.failure(Exception("Guild name already exists"))
        }
        if (guildTagIndex.containsKey(tag.lowercase())) {
            return Result.failure(Exception("Guild tag already exists"))
        }

        val guildId = transaction {
            val guild = Guild.new {
                this.name = name
                this.tag = tag
                this.leaderUUID = leader.toString()
                this.friendlyFireEnabled = false
            }

            GuildMember.new {
                this.playerUUID = leader.toString()
                this.guildId = guild.id
                this.role = GuildRole.LEADER
            }

            guild.id.value
        }

        val members = mutableMapOf(leader to GuildRole.LEADER)
        val guildInfo = GuildInfo(
            id = guildId,
            name = name,
            tag = tag,
            leaderUUID = leader,
            members = members,
            friendlyFireEnabled = false
        )

        guildCache[guildId] = guildInfo
        guildNameIndex[name.lowercase()] = guildId
        guildTagIndex[tag.lowercase()] = guildId
        playerGuildCache[leader] = guildId

        return Result.success(guildId)
    }

    /**
     * Invites a player to a guild.
     */
    fun invitePlayer(guildId: Int, inviter: UUID, invitee: UUID): Result<Unit> {
        val guild = guildCache[guildId] ?: return Result.failure(Exception(ERROR_GUILD_NOT_FOUND))
        val inviterRole = guild.members[inviter] ?: return Result.failure(Exception(ERROR_NOT_IN_GUILD))

        if (inviterRole != GuildRole.LEADER && inviterRole != GuildRole.ELDER) {
            return Result.failure(Exception("Only leaders and elders can invite players"))
        }

        if (playerGuildCache.containsKey(invitee)) {
            return Result.failure(Exception("Player is already in a guild"))
        }

        transaction {
            GuildMember.new {
                this.playerUUID = invitee.toString()
                this.guildId = EntityID(guildId, Guilds)
                this.role = GuildRole.MEMBER
            }
        }

        guild.members[invitee] = GuildRole.MEMBER
        playerGuildCache[invitee] = guildId

        return Result.success(Unit)
    }

    /**
     * Kicks a player from a guild.
     */
    fun kickPlayer(guildId: Int, kicker: UUID, target: UUID): Result<Unit> {
        val guild = guildCache[guildId] ?: return Result.failure(Exception(ERROR_GUILD_NOT_FOUND))
        val kickerRole = guild.members[kicker] ?: return Result.failure(Exception(ERROR_NOT_IN_GUILD))
        val targetRole = guild.members[target] ?: return Result.failure(Exception("Target is not in this guild"))

        // Leader can kick anyone, Elder can kick members and other elders but not leader
        when (kickerRole) {
            GuildRole.LEADER -> {} // Can kick anyone
            GuildRole.ELDER -> {
                if (targetRole == GuildRole.LEADER) {
                    return Result.failure(Exception("Elders cannot kick the leader"))
                }
            }

            GuildRole.MEMBER -> return Result.failure(Exception("Members cannot kick players"))
        }

        if (kicker == target) {
            return Result.failure(Exception("Use /guild leave to leave the guild"))
        }

        transaction {
            GuildMember.find {
                (GuildMembers.playerUUID eq target.toString()) and (GuildMembers.guildId eq guildId)
            }.firstOrNull()?.delete()
        }

        guild.members.remove(target)
        playerGuildCache.remove(target)

        // Check if guild is empty after kick
        if (guild.members.isEmpty()) {
            deleteGuild(guildId)
        }

        return Result.success(Unit)
    }

    /**
     * Promotes a member to elder or elder to leader.
     */
    fun promotePlayer(guildId: Int, promoter: UUID, target: UUID): Result<Unit> {
        val guild = guildCache[guildId] ?: return Result.failure(Exception(ERROR_GUILD_NOT_FOUND))
        val promoterRole = guild.members[promoter] ?: return Result.failure(Exception(ERROR_NOT_IN_GUILD))
        val targetRole = guild.members[target] ?: return Result.failure(Exception("Target is not in this guild"))

        return when (promoterRole) {
            GuildRole.LEADER -> handleLeaderPromotion(guildId, promoter, target, targetRole, guild)
            GuildRole.ELDER -> handleElderPromotion(guildId, target, targetRole, guild)
            GuildRole.MEMBER -> Result.failure(Exception("Members cannot promote players"))
        }
    }

    /**
     * Removes a player from their guild (leaving).
     */
    fun leaveGuild(player: UUID): Result<Unit> {
        val guildId = playerGuildCache[player] ?: return Result.failure(Exception("You are not in a guild"))
        val guild = guildCache[guildId] ?: return Result.failure(Exception(ERROR_GUILD_NOT_FOUND))
        val role = guild.members[player] ?: return Result.failure(Exception(ERROR_NOT_IN_GUILD))

        transaction {
            GuildMember.find {
                (GuildMembers.playerUUID eq player.toString()) and (GuildMembers.guildId eq guildId)
            }.firstOrNull()?.delete()
        }

        guild.members.remove(player)
        playerGuildCache.remove(player)

        // Handle leadership succession
        if (role == GuildRole.LEADER) {
            val newLeader = selectNewLeader(guild)
            if (newLeader != null) {
                setGuildLeader(guildId, guild, newLeader)
            } else {
                deleteGuild(guildId)
            }
        }

        // Check if guild is empty after leave
        if (guild.members.isEmpty()) {
            deleteGuild(guildId)
        }

        return Result.success(Unit)
    }

    /**
     * Toggles friendly fire for a guild.
     */
    fun toggleFriendlyFire(guildId: Int, player: UUID): Result<Boolean> {
        val guild = guildCache[guildId] ?: return Result.failure(Exception(ERROR_GUILD_NOT_FOUND))
        val role = guild.members[player] ?: return Result.failure(Exception(ERROR_NOT_IN_GUILD))

        if (role != GuildRole.LEADER) {
            return Result.failure(Exception("Only the leader can toggle friendly fire"))
        }

        val newValue = !guild.friendlyFireEnabled

        transaction {
            Guild.findById(guildId)?.friendlyFireEnabled = newValue
        }

        guild.friendlyFireEnabled = newValue

        return Result.success(newValue)
    }

    /**
     * Deletes a guild completely.
     */
    private fun deleteGuild(guildId: Int) {
        val guild = guildCache[guildId] ?: return

        transaction {
            GuildMember.find { GuildMembers.guildId eq guildId }.forEach { it.delete() }
            Guild.findById(guildId)?.delete()
        }

        guild.members.keys.forEach { playerGuildCache.remove(it) }
        guildNameIndex.remove(guild.name.lowercase())
        guildTagIndex.remove(guild.tag.lowercase())
        guildCache.remove(guildId)
    }

    /**
     * Gets the guild ID for a player.
     */
    fun getPlayerGuildId(player: UUID): Int? = playerGuildCache[player]

    /**
     * Gets guild info by ID.
     */
    fun getGuild(guildId: Int): GuildInfo? = guildCache[guildId]

    /**
     * Gets guild info for a player.
     */
    fun getPlayerGuild(player: UUID): GuildInfo? {
        val guildId = playerGuildCache[player] ?: return null
        return guildCache[guildId]
    }

    /**
     * Checks if two players are in the same guild.
     */
    fun areInSameGuild(player1: UUID, player2: UUID): Boolean {
        val guild1 = playerGuildCache[player1] ?: return false
        val guild2 = playerGuildCache[player2] ?: return false
        return guild1 == guild2
    }

    /**
     * Gets the formatted guild tag for a player.
     * Returns null if the player is not in a guild.
     */
    fun getFormattedTag(player: UUID): String? {
        val guildId = playerGuildCache[player] ?: return null
        val guild = guildCache[guildId] ?: return null
        return "[${guild.tag}]"
    }

    /**
     * Checks if friendly fire is enabled between two players.
     * Returns true if damage should be allowed, false if it should be blocked.
     */
    fun canDamage(attacker: UUID, victim: UUID): Boolean {
        if (!areInSameGuild(attacker, victim)) {
            return true // Not in same guild, damage allowed
        }

        val guildId = playerGuildCache[attacker] ?: return true
        val guild = guildCache[guildId] ?: return true

        return guild.friendlyFireEnabled
    }

    /**
     * Checks if a guild is the last team standing in a game.
     * This is used to enable friendly fire when only guild members remain.
     */
    fun isLastTeamStanding(guildId: Int, alivePlayers: List<Player>): Boolean {
        guildCache[guildId] ?: return false

        // Check if all alive players are in this guild
        return alivePlayers.all { playerGuildCache[it.uniqueId] == guildId }
    }

    private fun handleLeaderPromotion(
        guildId: Int,
        promoter: UUID,
        target: UUID,
        targetRole: GuildRole,
        guild: GuildInfo
    ): Result<Unit> {
        return when (targetRole) {
            GuildRole.MEMBER -> {
                transaction {
                    GuildMember.find {
                        (GuildMembers.playerUUID eq target.toString()) and (GuildMembers.guildId eq guildId)
                    }.firstOrNull()?.role = GuildRole.ELDER
                }
                guild.members[target] = GuildRole.ELDER
                Result.success(Unit)
            }

            GuildRole.ELDER -> {
                transaction {
                    GuildMember.find {
                        (GuildMembers.playerUUID eq target.toString()) and (GuildMembers.guildId eq guildId)
                    }.firstOrNull()?.role = GuildRole.LEADER

                    GuildMember.find {
                        (GuildMembers.playerUUID eq promoter.toString()) and (GuildMembers.guildId eq guildId)
                    }.firstOrNull()?.role = GuildRole.ELDER

                    Guild.findById(guildId)?.leaderUUID = target.toString()
                }
                guild.members[target] = GuildRole.LEADER
                guild.members[promoter] = GuildRole.ELDER
                Result.success(Unit)
            }

            GuildRole.LEADER -> Result.failure(Exception("Target is already the leader"))
        }
    }

    private fun handleElderPromotion(
        guildId: Int,
        target: UUID,
        targetRole: GuildRole,
        guild: GuildInfo
    ): Result<Unit> {
        if (targetRole != GuildRole.MEMBER) {
            return Result.failure(Exception("Elders can only promote members to elder"))
        }
        transaction {
            GuildMember.find {
                (GuildMembers.playerUUID eq target.toString()) and (GuildMembers.guildId eq guildId)
            }.firstOrNull()?.role = GuildRole.ELDER
        }
        guild.members[target] = GuildRole.ELDER
        return Result.success(Unit)
    }

    private fun selectNewLeader(guild: GuildInfo): UUID? {
        val elders = guild.members.filter { it.value == GuildRole.ELDER }
        return if (elders.isNotEmpty()) {
            elders.keys.random()
        } else {
            guild.members.keys.randomOrNull()
        }
    }

    private fun setGuildLeader(guildId: Int, guild: GuildInfo, newLeader: UUID) {
        transaction {
            GuildMember.find {
                (GuildMembers.playerUUID eq newLeader.toString()) and (GuildMembers.guildId eq guildId)
            }.firstOrNull()?.role = GuildRole.LEADER

            Guild.findById(guildId)?.leaderUUID = newLeader.toString()
        }
        guild.members[newLeader] = GuildRole.LEADER
    }
}
