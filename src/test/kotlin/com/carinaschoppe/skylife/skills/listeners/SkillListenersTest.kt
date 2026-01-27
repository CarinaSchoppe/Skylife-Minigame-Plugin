package com.carinaschoppe.skylife.skills.listeners

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.potion.PotionEffectType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SkillListenersTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Skylife
    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Skylife::class.java)
        player = server.addPlayer("TestPlayer")
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    // ========== SkillJumboListener Tests ==========

    @Test
    fun `test Jumbo prevents hunger loss`() {
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.activateSkills(player)

        player.foodLevel = 20
        val event = FoodLevelChangeEvent(player, 18)
        server.pluginManager.callEvent(event)

        assertTrue(event.isCancelled)
        assertEquals(20, player.foodLevel)
    }

    @Test
    fun `test Jumbo does not prevent hunger without skill active`() {
        player.foodLevel = 20
        val event = FoodLevelChangeEvent(player, 18)
        server.pluginManager.callEvent(event)

        assertFalse(event.isCancelled)
    }

    @Test
    fun `test Jumbo sets saturation to 20`() {
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.activateSkills(player)

        player.saturation = 0f
        val event = FoodLevelChangeEvent(player, 18)
        server.pluginManager.callEvent(event)

        assertEquals(20f, player.saturation)
    }

    // ========== SkillFeatherfallListener Tests ==========

    @Test
    fun `test Featherfall reduces fall damage by 50 percent`() {
        SkillsManager.toggleSkill(player, Skill.FEATHERFALL)
        SkillsManager.activateSkills(player)

        val event = EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 10.0)
        server.pluginManager.callEvent(event)

        assertEquals(5.0, event.damage, 0.01) // 50% of 10 = 5
    }

    @Test
    fun `test Featherfall does not reduce other damage types`() {
        SkillsManager.toggleSkill(player, Skill.FEATHERFALL)
        SkillsManager.activateSkills(player)

        val event = EntityDamageEvent(player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 10.0)
        server.pluginManager.callEvent(event)

        assertEquals(10.0, event.damage, 0.01) // No reduction
    }

    @Test
    fun `test Featherfall does not reduce fall damage without skill active`() {
        val event = EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 10.0)
        server.pluginManager.callEvent(event)

        assertEquals(10.0, event.damage, 0.01) // No reduction
    }

    // ========== SkillInvisibleStalkerListener Tests ==========

    @Test
    fun `test Invisible Stalker grants invisibility when sneaking`() {
        SkillsManager.toggleSkill(player, Skill.INVISIBLE_STALKER)
        SkillsManager.activateSkills(player)

        val event = PlayerToggleSneakEvent(player, true)
        server.pluginManager.callEvent(event)

        assertTrue(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
    }

    @Test
    fun `test Invisible Stalker removes invisibility when not sneaking`() {
        SkillsManager.toggleSkill(player, Skill.INVISIBLE_STALKER)
        SkillsManager.activateSkills(player)

        // Start sneaking
        val startSneak = PlayerToggleSneakEvent(player, true)
        server.pluginManager.callEvent(startSneak)
        assertTrue(player.hasPotionEffect(PotionEffectType.INVISIBILITY))

        // Stop sneaking
        val stopSneak = PlayerToggleSneakEvent(player, false)
        server.pluginManager.callEvent(stopSneak)
        assertFalse(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
    }

    @Test
    fun `test Invisible Stalker does not grant invisibility without skill active`() {
        val event = PlayerToggleSneakEvent(player, true)
        server.pluginManager.callEvent(event)

        assertFalse(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
    }

    // ========== Integration Tests ==========

    @Test
    fun `test multiple skill listeners work together`() {
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.toggleSkill(player, Skill.FEATHERFALL)
        SkillsManager.activateSkills(player)

        // Test Jumbo
        val foodEvent = FoodLevelChangeEvent(player, 18)
        server.pluginManager.callEvent(foodEvent)
        assertTrue(foodEvent.isCancelled)

        // Test Featherfall
        val fallEvent = EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 10.0)
        server.pluginManager.callEvent(fallEvent)
        assertEquals(5.0, fallEvent.damage, 0.01)
    }

    @Test
    fun `test skill effects only work when activated`() {
        // Select but don't activate
        SkillsManager.toggleSkill(player, Skill.JUMBO)

        val event = FoodLevelChangeEvent(player, 18)
        server.pluginManager.callEvent(event)

        assertFalse(event.isCancelled, "Skill should not work when not activated")
    }

    @Test
    fun `test skill effects stop working after deactivation`() {
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.activateSkills(player)

        // First event should be cancelled
        val event1 = FoodLevelChangeEvent(player, 18)
        server.pluginManager.callEvent(event1)
        assertTrue(event1.isCancelled)

        // Deactivate
        SkillsManager.deactivateSkills(player)

        // Second event should not be cancelled
        val event2 = FoodLevelChangeEvent(player, 18)
        server.pluginManager.callEvent(event2)
        assertFalse(event2.isCancelled)
    }
}
