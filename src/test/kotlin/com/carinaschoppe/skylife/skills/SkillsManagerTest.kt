package com.carinaschoppe.skylife.skills

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.carinaschoppe.skylife.Skylife
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class SkillsManagerTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Skylife

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Skylife::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `test max skills limit is 2`() {
        assertEquals(2, SkillsManager.MAX_SKILLS)
    }

    @Test
    fun `test player starts with no skills selected`() {
        val player = server.addPlayer("TestPlayer")
        val skills = SkillsManager.getSelectedSkills(player)
        assertTrue(skills.isEmpty())
    }

    @Test
    fun `test player can select first skill`() {
        val player = server.addPlayer("TestPlayer")
        val result = SkillsManager.toggleSkill(player, Skill.JUMBO)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!) // true = selected
        assertTrue(SkillsManager.hasSkillSelected(player, Skill.JUMBO))
    }

    @Test
    fun `test player can select second skill`() {
        val player = server.addPlayer("TestPlayer")

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        val result = SkillsManager.toggleSkill(player, Skill.REGENERATOR)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)
        assertTrue(SkillsManager.hasSkillSelected(player, Skill.JUMBO))
        assertTrue(SkillsManager.hasSkillSelected(player, Skill.REGENERATOR))
    }

    @Test
    fun `test player cannot select third skill`() {
        val player = server.addPlayer("TestPlayer")

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        val result = SkillsManager.toggleSkill(player, Skill.ABSORBER)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains("already have 2 skills"))
        assertFalse(SkillsManager.hasSkillSelected(player, Skill.ABSORBER))
    }

    @Test
    fun `test player can unselect skill`() {
        val player = server.addPlayer("TestPlayer")

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        val result = SkillsManager.toggleSkill(player, Skill.JUMBO)

        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!) // false = unselected
        assertFalse(SkillsManager.hasSkillSelected(player, Skill.JUMBO))
    }

    @Test
    fun `test player can select third skill after unselecting one`() {
        val player = server.addPlayer("TestPlayer")

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        SkillsManager.toggleSkill(player, Skill.JUMBO) // Unselect
        val result = SkillsManager.toggleSkill(player, Skill.ABSORBER)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)
        assertFalse(SkillsManager.hasSkillSelected(player, Skill.JUMBO))
        assertTrue(SkillsManager.hasSkillSelected(player, Skill.REGENERATOR))
        assertTrue(SkillsManager.hasSkillSelected(player, Skill.ABSORBER))
    }

    @Test
    fun `test getSelectedSkills returns correct set`() {
        val player = server.addPlayer("TestPlayer")

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)

        val skills = SkillsManager.getSelectedSkills(player)
        assertEquals(2, skills.size)
        assertTrue(skills.contains(Skill.JUMBO))
        assertTrue(skills.contains(Skill.REGENERATOR))
    }

    @Test
    fun `test skills are not active before activation`() {
        val player = server.addPlayer("TestPlayer")
        SkillsManager.toggleSkill(player, Skill.JUMBO)

        assertFalse(SkillsManager.hasSkillActive(player, Skill.JUMBO))
    }

    @Test
    fun `test skills become active after activation`() {
        val player = server.addPlayer("TestPlayer")
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.activateSkills(player)

        assertTrue(SkillsManager.hasSkillActive(player, Skill.JUMBO))
    }

    @Test
    fun `test skills become inactive after deactivation`() {
        val player = server.addPlayer("TestPlayer")
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.activateSkills(player)
        SkillsManager.deactivateSkills(player)

        assertFalse(SkillsManager.hasSkillActive(player, Skill.JUMBO))
    }

    @Test
    fun `test getActiveSkills returns correct set`() {
        val player = server.addPlayer("TestPlayer")
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        SkillsManager.activateSkills(player)

        val activeSkills = SkillsManager.getActiveSkills(player)
        assertEquals(2, activeSkills.size)
        assertTrue(activeSkills.contains(Skill.JUMBO))
        assertTrue(activeSkills.contains(Skill.REGENERATOR))
    }

    @Test
    fun `test clearSkills removes all skills`() {
        val player = server.addPlayer("TestPlayer")
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        SkillsManager.activateSkills(player)

        SkillsManager.clearSkills(player.uniqueId)

        assertTrue(SkillsManager.getSelectedSkills(player).isEmpty())
        assertTrue(SkillsManager.getActiveSkills(player).isEmpty())
    }

    @Test
    fun `test getSelectedSkills by UUID`() {
        val player = server.addPlayer("TestPlayer")
        SkillsManager.toggleSkill(player, Skill.JUMBO)

        val skills = SkillsManager.getSelectedSkills(player.uniqueId)
        assertEquals(1, skills.size)
        assertTrue(skills.contains(Skill.JUMBO))
    }

    @Test
    fun `test multiple players can have different skills`() {
        val player1 = server.addPlayer("Player1")
        val player2 = server.addPlayer("Player2")

        SkillsManager.toggleSkill(player1, Skill.JUMBO)
        SkillsManager.toggleSkill(player2, Skill.REGENERATOR)

        assertTrue(SkillsManager.hasSkillSelected(player1, Skill.JUMBO))
        assertFalse(SkillsManager.hasSkillSelected(player1, Skill.REGENERATOR))
        assertFalse(SkillsManager.hasSkillSelected(player2, Skill.JUMBO))
        assertTrue(SkillsManager.hasSkillSelected(player2, Skill.REGENERATOR))
    }
}
