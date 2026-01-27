package com.carinaschoppe.skylife.skills

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.carinaschoppe.skylife.Skylife
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class SkillDatabaseTest {

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
    fun `test PlayerSkills table exists`() {
        transaction {
            // Should not crash
            assertTrue(PlayerSkills.exists())
        }
    }

    @Test
    fun `test skill selection persists to database`() {
        val player = server.addPlayer("TestPlayer")
        val uuid = player.uniqueId

        SkillsManager.toggleSkill(player, Skill.JUMBO)

        // Check database
        transaction {
            val record = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()
            assertNotNull(record)
            assertEquals(Skill.JUMBO, record!!.skill1)
            assertNull(record.skill2)
        }
    }

    @Test
    fun `test two skills persist to database`() {
        val player = server.addPlayer("TestPlayer")
        val uuid = player.uniqueId

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)

        transaction {
            val record = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()
            assertNotNull(record)

            // Both skills should be stored (order may vary)
            val skills = setOfNotNull(record!!.skill1, record.skill2)
            assertEquals(2, skills.size)
            assertTrue(skills.contains(Skill.JUMBO))
            assertTrue(skills.contains(Skill.REGENERATOR))
        }
    }

    @Test
    fun `test skill unselection updates database`() {
        val player = server.addPlayer("TestPlayer")
        val uuid = player.uniqueId

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        SkillsManager.toggleSkill(player, Skill.JUMBO) // Unselect

        transaction {
            val record = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()
            assertNotNull(record)

            // Only Regenerator should remain
            val skills = setOfNotNull(record!!.skill1, record.skill2)
            assertEquals(1, skills.size)
            assertTrue(skills.contains(Skill.REGENERATOR))
            assertFalse(skills.contains(Skill.JUMBO))
        }
    }

    @Test
    fun `test all skills unselected clears database entry`() {
        val player = server.addPlayer("TestPlayer")
        val uuid = player.uniqueId

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.JUMBO) // Unselect

        transaction {
            val record = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()
            if (record != null) {
                // Both skills should be null
                assertNull(record.skill1)
                assertNull(record.skill2)
            }
            // Or record might not exist at all, which is also acceptable
        }
    }

    @Test
    fun `test loadSkills loads from database`() {
        val uuid = UUID.randomUUID()

        // Manually insert into database
        transaction {
            PlayerSkillSelection.new {
                this.playerUUID = uuid.toString()
                this.skill1 = Skill.JUMBO
                this.skill2 = Skill.REGENERATOR
            }
        }

        // Load skills
        SkillsManager.loadSkills()

        // Check in-memory cache
        val skills = SkillsManager.getSelectedSkills(uuid)
        assertEquals(2, skills.size)
        assertTrue(skills.contains(Skill.JUMBO))
        assertTrue(skills.contains(Skill.REGENERATOR))
    }

    @Test
    fun `test loadSkills handles single skill`() {
        val uuid = UUID.randomUUID()

        transaction {
            PlayerSkillSelection.new {
                this.playerUUID = uuid.toString()
                this.skill1 = Skill.JUMBO
                this.skill2 = null
            }
        }

        SkillsManager.loadSkills()

        val skills = SkillsManager.getSelectedSkills(uuid)
        assertEquals(1, skills.size)
        assertTrue(skills.contains(Skill.JUMBO))
    }

    @Test
    fun `test loadSkills handles empty skills`() {
        val uuid = UUID.randomUUID()

        transaction {
            PlayerSkillSelection.new {
                this.playerUUID = uuid.toString()
                this.skill1 = null
                this.skill2 = null
            }
        }

        SkillsManager.loadSkills()

        val skills = SkillsManager.getSelectedSkills(uuid)
        assertTrue(skills.isEmpty())
    }

    @Test
    fun `test multiple players have separate database records`() {
        val player1 = server.addPlayer("Player1")
        val player2 = server.addPlayer("Player2")

        SkillsManager.toggleSkill(player1, Skill.JUMBO)
        SkillsManager.toggleSkill(player2, Skill.REGENERATOR)

        transaction {
            val record1 = PlayerSkillSelection.find { PlayerSkills.playerUUID eq player1.uniqueId.toString() }
                .firstOrNull()
            val record2 = PlayerSkillSelection.find { PlayerSkills.playerUUID eq player2.uniqueId.toString() }
                .firstOrNull()

            assertNotNull(record1)
            assertNotNull(record2)
            assertNotEquals(record1!!.id, record2!!.id)

            val skills1 = setOfNotNull(record1.skill1, record1.skill2)
            val skills2 = setOfNotNull(record2.skill1, record2.skill2)

            assertTrue(skills1.contains(Skill.JUMBO))
            assertFalse(skills1.contains(Skill.REGENERATOR))
            assertFalse(skills2.contains(Skill.JUMBO))
            assertTrue(skills2.contains(Skill.REGENERATOR))
        }
    }

    @Test
    fun `test clearSkills removes database record`() {
        val player = server.addPlayer("TestPlayer")
        val uuid = player.uniqueId

        SkillsManager.toggleSkill(player, Skill.JUMBO)

        // Verify record exists
        transaction {
            val record = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()
            assertNotNull(record)
        }

        // Clear skills
        SkillsManager.clearSkills(uuid)

        // Verify record is deleted
        transaction {
            val record = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()
            assertNull(record)
        }
    }

    @Test
    fun `test database persists across server restarts`() {
        val player = server.addPlayer("TestPlayer")
        val uuid = player.uniqueId

        // Select skills
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)

        // Simulate server restart by reloading skills
        SkillsManager.loadSkills()

        // Skills should still be loaded
        val skills = SkillsManager.getSelectedSkills(uuid)
        assertEquals(2, skills.size)
        assertTrue(skills.contains(Skill.JUMBO))
        assertTrue(skills.contains(Skill.REGENERATOR))
    }

    @Test
    fun `test playerUUID is unique in database`() {
        val player = server.addPlayer("TestPlayer")

        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)

        // Count records for this player
        transaction {
            val count = PlayerSkillSelection.find { PlayerSkills.playerUUID eq player.uniqueId.toString() }.count()
            assertEquals(1, count, "Should only have one record per player")
        }
    }

    @Test
    fun `test database handles all 14 skills`() {
        val uuid = UUID.randomUUID()

        // Test each skill can be stored
        Skill.values().forEach { skill ->
            transaction {
                PlayerSkillSelection.new {
                    this.playerUUID = uuid.toString()
                    this.skill1 = skill
                    this.skill2 = null
                }
            }

            // Verify it was stored
            transaction {
                val record = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()
                assertNotNull(record)
                assertEquals(skill, record!!.skill1)
            }

            // Clean up for next iteration
            SkillsManager.clearSkills(uuid)
        }
    }
}
