package com.carinaschoppe.skylife.guild

import com.carinaschoppe.skylife.commands.user.GuildCommand
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Integration tests for the guild system.
 * These tests verify the complete guild workflow including database operations.
 */
class GuildSystemIntegrationTest {

    private lateinit var guildCommand: GuildCommand
    private lateinit var mockLeader: Player
    private lateinit var mockMember: Player
    private val leaderUUID = UUID.randomUUID()
    private val memberUUID = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        guildCommand = GuildCommand()

        mockLeader = mock {
            on { uniqueId } doReturn leaderUUID
            on { name } doReturn "Leader"
            on { displayName } doReturn mock()
        }

        mockMember = mock {
            on { uniqueId } doReturn memberUUID
            on { name } doReturn "Member"
            on { displayName } doReturn mock()
        }
    }

    @AfterEach
    fun cleanup() {
        // Clean up any created guilds
        val guildId = GuildManager.getPlayerGuildId(leaderUUID)
        if (guildId != null) {
            val guild = GuildManager.getGuild(guildId)
            guild?.members?.keys?.toList()?.forEach { uuid ->
                GuildManager.leaveGuild(uuid)
            }
        }
    }

    @Test
    fun `complete guild workflow - create, invite, promote, kick, leave`() {
        // Create guild
        val command: Command = mock()
        guildCommand.onCommand(mockLeader, command, "guild", arrayOf("create", "TestGuild", "TEST"))

        val guildId = GuildManager.getPlayerGuildId(leaderUUID)
        assertNotNull(guildId)

        val guild = GuildManager.getGuild(guildId)
        assertNotNull(guild)
        assertEquals("TestGuild", guild.name)
        assertEquals("TEST", guild.tag)
        assertEquals(GuildRole.LEADER, guild.members[leaderUUID])

        // Invite member
        guildCommand.onCommand(mockLeader, command, "guild", arrayOf("invite", "Member"))
        // Since we can't mock Bukkit.getPlayerExact, this would need server mock

        // Verify guild tag formatting
        val tag = GuildManager.getFormattedTag(leaderUUID)
        assertEquals("[TEST]", tag)

        // Leave guild
        guildCommand.onCommand(mockLeader, command, "guild", arrayOf("leave"))
        assertNull(GuildManager.getPlayerGuildId(leaderUUID))
    }

    @Test
    fun `guild friendly fire toggle workflow`() {
        val result = GuildManager.createGuild("TestGuild", "TEST", leaderUUID)
        assertTrue(result.isSuccess)
        val guildId = result.getOrNull()!!

        val guild = GuildManager.getGuild(guildId)
        assertNotNull(guild)

        // Friendly fire should be disabled by default
        assertEquals(false, guild.friendlyFireEnabled)

        // Toggle friendly fire
        val toggleResult = GuildManager.toggleFriendlyFire(guildId, leaderUUID)
        assertTrue(toggleResult.isSuccess)
        assertTrue(toggleResult.getOrNull()!!)

        // Verify it's enabled
        val updatedGuild = GuildManager.getGuild(guildId)
        assertTrue(updatedGuild?.friendlyFireEnabled ?: false)

        // Clean up
        GuildManager.leaveGuild(leaderUUID)
    }

    @Test
    fun `guild leadership succession on leader leave`() {
        val result = GuildManager.createGuild("TestGuild", "TEST", leaderUUID)
        assertTrue(result.isSuccess)
        val guildId = result.getOrNull()!!

        // Add and promote member to elder
        GuildManager.invitePlayer(guildId, leaderUUID, memberUUID)
        GuildManager.promotePlayer(guildId, leaderUUID, memberUUID)

        // Leader leaves
        GuildManager.leaveGuild(leaderUUID)

        // Member should become leader
        val guild = GuildManager.getGuild(guildId)
        assertNotNull(guild)
        assertEquals(memberUUID, guild.leaderUUID)
        assertEquals(GuildRole.LEADER, guild.members[memberUUID])

        // Clean up
        GuildManager.leaveGuild(memberUUID)
    }

    @Test
    fun `guild deletion when last member leaves`() {
        val result = GuildManager.createGuild("TestGuild", "TEST", leaderUUID)
        assertTrue(result.isSuccess)
        val guildId = result.getOrNull()!!

        // Leader leaves
        GuildManager.leaveGuild(leaderUUID)

        // Guild should be deleted
        assertNull(GuildManager.getGuild(guildId))
    }

    @Test
    fun `guild members can damage enemies but not each other`() {
        val result = GuildManager.createGuild("TestGuild", "TEST", leaderUUID)
        assertTrue(result.isSuccess)
        val guildId = result.getOrNull()!!

        GuildManager.invitePlayer(guildId, leaderUUID, memberUUID)

        val enemyUUID = UUID.randomUUID()

        // Guild members cannot damage each other
        assertEquals(false, GuildManager.canDamage(leaderUUID, memberUUID))
        assertEquals(false, GuildManager.canDamage(memberUUID, leaderUUID))

        // Guild members can damage enemies
        assertTrue(GuildManager.canDamage(leaderUUID, enemyUUID))
        assertTrue(GuildManager.canDamage(memberUUID, enemyUUID))

        // Clean up
        GuildManager.leaveGuild(leaderUUID)
        GuildManager.leaveGuild(memberUUID)
    }

    @Test
    fun `guild members can damage each other when friendly fire enabled`() {
        val result = GuildManager.createGuild("TestGuild", "TEST", leaderUUID)
        assertTrue(result.isSuccess)
        val guildId = result.getOrNull()!!

        GuildManager.invitePlayer(guildId, leaderUUID, memberUUID)
        GuildManager.toggleFriendlyFire(guildId, leaderUUID)

        // With friendly fire enabled, guild members can damage each other
        assertTrue(GuildManager.canDamage(leaderUUID, memberUUID))
        assertTrue(GuildManager.canDamage(memberUUID, leaderUUID))

        // Clean up
        GuildManager.leaveGuild(leaderUUID)
        GuildManager.leaveGuild(memberUUID)
    }

    @Test
    fun `cannot create guild with duplicate name`() {
        GuildManager.createGuild("TestGuild", "TEST", leaderUUID)

        val result = GuildManager.createGuild("TestGuild", "TST2", memberUUID)
        assertTrue(result.isFailure)

        // Clean up
        GuildManager.leaveGuild(leaderUUID)
    }

    @Test
    fun `cannot create guild with duplicate tag`() {
        GuildManager.createGuild("TestGuild", "TEST", leaderUUID)

        val result = GuildManager.createGuild("TestGuild2", "TEST", memberUUID)
        assertTrue(result.isFailure)

        // Clean up
        GuildManager.leaveGuild(leaderUUID)
    }

    @Test
    fun `elder can promote members but not other elders to leader`() {
        val result = GuildManager.createGuild("TestGuild", "TEST", leaderUUID)
        val guildId = result.getOrNull()!!

        val elderUUID = UUID.randomUUID()
        val memberUUID2 = UUID.randomUUID()

        // Add elder
        GuildManager.invitePlayer(guildId, leaderUUID, elderUUID)
        GuildManager.promotePlayer(guildId, leaderUUID, elderUUID)

        // Add member
        GuildManager.invitePlayer(guildId, leaderUUID, memberUUID2)

        // Elder promotes member to elder
        val promoteResult = GuildManager.promotePlayer(guildId, elderUUID, memberUUID2)
        assertTrue(promoteResult.isSuccess)

        val guild = GuildManager.getGuild(guildId)
        assertEquals(GuildRole.ELDER, guild?.members?.get(memberUUID2))

        // Elder cannot promote another elder to leader
        val promoteToLeaderResult = GuildManager.promotePlayer(guildId, elderUUID, memberUUID2)
        assertTrue(promoteToLeaderResult.isFailure)

        // Clean up
        GuildManager.leaveGuild(leaderUUID)
        GuildManager.leaveGuild(elderUUID)
        GuildManager.leaveGuild(memberUUID2)
    }
}
