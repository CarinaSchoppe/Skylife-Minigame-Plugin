package com.carinaschoppe.skylife.guild

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.*

class GuildManagerTest {

    private lateinit var leader: UUID
    private lateinit var member1: UUID
    private lateinit var member2: UUID
    private lateinit var nonMember: UUID

    @BeforeEach
    fun setup() {
        leader = UUID.randomUUID()
        member1 = UUID.randomUUID()
        member2 = UUID.randomUUID()
        nonMember = UUID.randomUUID()
    }

    @Test
    fun `createGuild creates guild with valid name and tag`() {
        val result = GuildManager.createGuild("TestGuild", "TEST", leader)
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())

        val guild = GuildManager.getPlayerGuild(leader)
        assertNotNull(guild)
        assertEquals("TestGuild", guild.name)
        assertEquals("TEST", guild.tag)
        assertEquals(leader, guild.leaderUUID)
        assertEquals(GuildRole.LEADER, guild.members[leader])
    }

    @Test
    fun `createGuild fails with name longer than 24 chars`() {
        val longName = "a".repeat(25)
        val result = GuildManager.createGuild(longName, "TAG", leader)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createGuild fails with tag longer than 5 chars`() {
        val result = GuildManager.createGuild("Guild", "TOOLONG", leader)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createGuild fails with duplicate name`() {
        GuildManager.createGuild("TestGuild", "TEST", leader)
        val result = GuildManager.createGuild("TestGuild", "TST2", member1)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createGuild fails with duplicate tag`() {
        GuildManager.createGuild("TestGuild", "TEST", leader)
        val result = GuildManager.createGuild("TestGuild2", "TEST", member1)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createGuild fails if player already in guild`() {
        GuildManager.createGuild("TestGuild", "TEST", leader)
        val result = GuildManager.createGuild("TestGuild2", "TST2", leader)
        assertTrue(result.isFailure)
    }

    @Test
    fun `invitePlayer successfully adds member to guild`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        val result = GuildManager.invitePlayer(guildId, leader, member1)
        assertTrue(result.isSuccess)

        val guild = GuildManager.getGuild(guildId)
        assertNotNull(guild)
        assertEquals(GuildRole.MEMBER, guild.members[member1])
    }

    @Test
    fun `invitePlayer fails if inviter not in guild`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        val result = GuildManager.invitePlayer(guildId, nonMember, member1)
        assertTrue(result.isFailure)
    }

    @Test
    fun `invitePlayer fails if invitee already in guild`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        val result = GuildManager.invitePlayer(guildId, leader, member1)
        assertTrue(result.isFailure)
    }

    @Test
    fun `elder can invite players`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        // Add member1 and promote to elder
        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1)

        // Elder invites member2
        val result = GuildManager.invitePlayer(guildId, member1, member2)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `member cannot invite players`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)

        val result = GuildManager.invitePlayer(guildId, member1, member2)
        assertTrue(result.isFailure)
    }

    @Test
    fun `kickPlayer removes member from guild`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        val result = GuildManager.kickPlayer(guildId, leader, member1)
        assertTrue(result.isSuccess)

        val guild = GuildManager.getGuild(guildId)
        assertNull(guild?.members?.get(member1))
    }

    @Test
    fun `leader can kick anyone`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1) // Make elder

        val result = GuildManager.kickPlayer(guildId, leader, member1)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `elder cannot kick leader`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1) // Make elder

        val result = GuildManager.kickPlayer(guildId, member1, leader)
        assertTrue(result.isFailure)
    }

    @Test
    fun `elder can kick members and other elders`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1) // Make elder
        GuildManager.invitePlayer(guildId, leader, member2)

        val result = GuildManager.kickPlayer(guildId, member1, member2)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `member cannot kick anyone`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.invitePlayer(guildId, leader, member2)

        val result = GuildManager.kickPlayer(guildId, member1, member2)
        assertTrue(result.isFailure)
    }

    @Test
    fun `promotePlayer promotes member to elder`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        val result = GuildManager.promotePlayer(guildId, leader, member1)
        assertTrue(result.isSuccess)

        val guild = GuildManager.getGuild(guildId)
        assertEquals(GuildRole.ELDER, guild?.members?.get(member1))
    }

    @Test
    fun `promotePlayer promotes elder to leader and demotes old leader`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1) // Make elder
        val result = GuildManager.promotePlayer(guildId, leader, member1) // Make leader
        assertTrue(result.isSuccess)

        val guild = GuildManager.getGuild(guildId)
        assertEquals(GuildRole.LEADER, guild?.members?.get(member1))
        assertEquals(GuildRole.ELDER, guild?.members?.get(leader))
        assertEquals(member1, guild?.leaderUUID)
    }

    @Test
    fun `elder can promote members to elder`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1) // Make elder
        GuildManager.invitePlayer(guildId, leader, member2)

        val result = GuildManager.promotePlayer(guildId, member1, member2)
        assertTrue(result.isSuccess)

        val guild = GuildManager.getGuild(guildId)
        assertEquals(GuildRole.ELDER, guild?.members?.get(member2))
    }

    @Test
    fun `elder cannot promote to leader`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1) // Make elder
        GuildManager.invitePlayer(guildId, leader, member2)
        GuildManager.promotePlayer(guildId, member1, member2) // Make elder

        val result = GuildManager.promotePlayer(guildId, member1, member2) // Try to make leader
        assertTrue(result.isFailure)
    }

    @Test
    fun `leaveGuild removes player from guild`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        val result = GuildManager.leaveGuild(member1)
        assertTrue(result.isSuccess)

        assertNull(GuildManager.getPlayerGuildId(member1))
    }

    @Test
    fun `leaveGuild with leader promotes elder to leader`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.promotePlayer(guildId, leader, member1) // Make elder

        GuildManager.leaveGuild(leader)

        val guild = GuildManager.getGuild(guildId)
        assertEquals(GuildRole.LEADER, guild?.members?.get(member1))
        assertEquals(member1, guild?.leaderUUID)
    }

    @Test
    fun `leaveGuild with leader promotes random member if no elders`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)

        GuildManager.leaveGuild(leader)

        val guild = GuildManager.getGuild(guildId)
        assertEquals(GuildRole.LEADER, guild?.members?.get(member1))
    }

    @Test
    fun `leaveGuild deletes guild when last member leaves`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.leaveGuild(leader)

        assertNull(GuildManager.getGuild(guildId))
    }

    @Test
    fun `toggleFriendlyFire changes friendly fire setting`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        val result = GuildManager.toggleFriendlyFire(guildId, leader)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)

        val guild = GuildManager.getGuild(guildId)
        assertTrue(guild?.friendlyFireEnabled ?: false)
    }

    @Test
    fun `only leader can toggle friendly fire`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)

        val result = GuildManager.toggleFriendlyFire(guildId, member1)
        assertTrue(result.isFailure)
    }

    @Test
    fun `areInSameGuild returns true for guild members`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)

        assertTrue(GuildManager.areInSameGuild(leader, member1))
    }

    @Test
    fun `areInSameGuild returns false for non-guild members`() {
        GuildManager.createGuild("TestGuild", "TEST", leader)

        assertFalse(GuildManager.areInSameGuild(leader, nonMember))
    }

    @Test
    fun `getFormattedTag returns correct tag format`() {
        GuildManager.createGuild("TestGuild", "TEST", leader)

        val tag = GuildManager.getFormattedTag(leader)
        assertEquals("[TEST]", tag)
    }

    @Test
    fun `getFormattedTag returns null for non-guild member`() {
        val tag = GuildManager.getFormattedTag(nonMember)
        assertNull(tag)
    }

    @Test
    fun `canDamage returns false when friendly fire disabled`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)

        assertFalse(GuildManager.canDamage(leader, member1))
    }

    @Test
    fun `canDamage returns true when friendly fire enabled`() {
        val guildResult = GuildManager.createGuild("TestGuild", "TEST", leader)
        val guildId = guildResult.getOrNull()!!

        GuildManager.invitePlayer(guildId, leader, member1)
        GuildManager.toggleFriendlyFire(guildId, leader)

        assertTrue(GuildManager.canDamage(leader, member1))
    }

    @Test
    fun `canDamage returns true for players not in same guild`() {
        GuildManager.createGuild("TestGuild", "TEST", leader)

        assertTrue(GuildManager.canDamage(leader, nonMember))
    }
}
