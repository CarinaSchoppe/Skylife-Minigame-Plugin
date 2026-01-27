package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.Skylife
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

class SkillEffectsManagerTest {

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

    @Test
    fun `test applySkillEffects with no skills does nothing`() {
        SkillEffectsManager.applySkillEffects(player)
        // Should not crash
        assertTrue(true)
    }

    @Test
    fun `test Jumbo skill sets food level to 20`() {
        SkillsManager.toggleSkill(player, Skill.JUMBO)
        SkillsManager.activateSkills(player)

        player.foodLevel = 10 // Set low
        SkillEffectsManager.applySkillEffects(player)

        assertEquals(20, player.foodLevel)
        assertEquals(20f, player.saturation)
    }

    @Test
    fun `test Regenerator skill applies regeneration potion effect`() {
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)

        assertTrue(player.hasPotionEffect(PotionEffectType.REGENERATION))
        val effect = player.getPotionEffect(PotionEffectType.REGENERATION)
        assertNotNull(effect)
        assertEquals(0, effect!!.amplifier) // Regeneration I = amplifier 0
    }

    @Test
    fun `test Absorber skill applies absorption potion effect`() {
        SkillsManager.toggleSkill(player, Skill.ABSORBER)
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)

        assertTrue(player.hasPotionEffect(PotionEffectType.ABSORPTION))
        val effect = player.getPotionEffect(PotionEffectType.ABSORPTION)
        assertNotNull(effect)
        assertEquals(0, effect!!.amplifier)
    }

    @Test
    fun `test Strength Core skill applies strength potion effect`() {
        SkillsManager.toggleSkill(player, Skill.STRENGTH_CORE)
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)

        assertTrue(player.hasPotionEffect(PotionEffectType.STRENGTH))
        val effect = player.getPotionEffect(PotionEffectType.STRENGTH)
        assertNotNull(effect)
        assertEquals(0, effect!!.amplifier) // Strength I = amplifier 0
    }

    @Test
    fun `test Lucky Bird skill gives lucky egg`() {
        SkillsManager.toggleSkill(player, Skill.LUCKY_BIRD)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        val eggs = player.inventory.contents.filterNotNull().filter { it.type == Material.EGG }
        assertTrue(eggs.isNotEmpty(), "Player should have egg")
    }

    @Test
    fun `test Wolflord skill gives bones`() {
        SkillsManager.toggleSkill(player, Skill.WOLFLORD)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        val bones = player.inventory.contents.filterNotNull().filter { it.type == Material.BONE }
        assertTrue(bones.isNotEmpty(), "Player should have bones")
        assertTrue(bones.size >= 4, "Player should have at least 4 stacks of bones")
    }

    @Test
    fun `test Builder skill gives 64 blocks`() {
        SkillsManager.toggleSkill(player, Skill.BUILDER)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        val blocks = player.inventory.contents.filterNotNull().filter { it.type == Material.STONE_BRICKS }
        assertTrue(blocks.isNotEmpty(), "Player should have stone bricks")
        assertEquals(64, blocks.first().amount)
    }

    @Test
    fun `test Swordmaster skill gives sword and shield`() {
        SkillsManager.toggleSkill(player, Skill.SWORDMASTER)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        val sword = player.inventory.contents.filterNotNull().firstOrNull { it.type == Material.IRON_SWORD }
        val shield = player.inventory.contents.filterNotNull().firstOrNull { it.type == Material.SHIELD }

        assertNotNull(sword, "Player should have sword")
        assertNotNull(shield, "Player should have shield")
        assertTrue(sword!!.containsEnchantment(Enchantment.SHARPNESS))
        assertEquals(1, sword.getEnchantmentLevel(Enchantment.SHARPNESS))
    }

    @Test
    fun `test Bowmaster skill gives bow and arrows`() {
        SkillsManager.toggleSkill(player, Skill.BOWMASTER)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        val bow = player.inventory.contents.filterNotNull().firstOrNull { it.type == Material.BOW }
        val arrows = player.inventory.contents.filterNotNull().firstOrNull { it.type == Material.ARROW }

        assertNotNull(bow, "Player should have bow")
        assertNotNull(arrows, "Player should have arrows")
        assertTrue(bow!!.containsEnchantment(Enchantment.INFINITY))
        assertTrue(bow.containsEnchantment(Enchantment.POWER))
        assertEquals(64, arrows!!.amount)
    }

    @Test
    fun `test removeSkillEffects removes all potion effects`() {
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)

        assertTrue(player.hasPotionEffect(PotionEffectType.REGENERATION))

        SkillEffectsManager.removeSkillEffects(player)

        assertFalse(player.hasPotionEffect(PotionEffectType.REGENERATION))
    }

    @Test
    fun `test removeSkillEffects resets food level`() {
        player.foodLevel = 10
        player.saturation = 0f

        SkillEffectsManager.removeSkillEffects(player)

        assertEquals(20, player.foodLevel)
        assertEquals(5f, player.saturation)
    }

    @Test
    fun `test multiple skills apply together`() {
        SkillsManager.toggleSkill(player, Skill.REGENERATOR)
        SkillsManager.toggleSkill(player, Skill.ABSORBER)
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)

        assertTrue(player.hasPotionEffect(PotionEffectType.REGENERATION))
        assertTrue(player.hasPotionEffect(PotionEffectType.ABSORPTION))
    }

    @Test
    fun `test Featherfall skill does not apply potion effect`() {
        // Featherfall is handled by listener, not by applySkillEffects
        SkillsManager.toggleSkill(player, Skill.FEATHERFALL)
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)

        // Should not crash, and no special effect should be applied
        assertTrue(true)
    }

    @Test
    fun `test Snow Spammer skill does not apply immediate effect`() {
        // Snow Spammer is handled by passive task, not by applySkillEffects
        SkillsManager.toggleSkill(player, Skill.SNOW_SPAMMER)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        // Should not give snowballs immediately
        val snowballs = player.inventory.contents.filterNotNull().filter { it.type == Material.SNOWBALL }
        assertTrue(snowballs.isEmpty())
    }

    @Test
    fun `test Endermaster skill does not apply immediate effect`() {
        // Endermaster is handled by passive task
        SkillsManager.toggleSkill(player, Skill.ENDERMASTER)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        // Should not give ender pearls immediately
        val pearls = player.inventory.contents.filterNotNull().filter { it.type == Material.ENDER_PEARL }
        assertTrue(pearls.isEmpty())
    }

    @Test
    fun `test Witch skill does not apply immediate effect`() {
        // Witch is handled by passive task
        SkillsManager.toggleSkill(player, Skill.WITCH)
        SkillsManager.activateSkills(player)
        player.inventory.clear()
        SkillEffectsManager.applySkillEffects(player)

        // Should not give potions immediately
        val potions = player.inventory.contents.filterNotNull().filter {
            it.type == Material.POTION || it.type == Material.SPLASH_POTION
        }
        assertTrue(potions.isEmpty())
    }

    @Test
    fun `test Invisible Stalker skill does not apply immediate effect`() {
        // Invisible Stalker is handled by listener
        SkillsManager.toggleSkill(player, Skill.INVISIBLE_STALKER)
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)

        // Should not have invisibility yet (only when sneaking)
        assertFalse(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
    }
}
