package com.carinaschoppe.skylife.skills

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.carinaschoppe.skylife.Skylife
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SkillTest {

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
    fun `test all 14 skills exist`() {
        assertEquals(14, Skill.values().size)
    }

    @Test
    fun `test all skills have correct materials`() {
        assertEquals(Material.BREAD, Skill.JUMBO.material)
        assertEquals(Material.HEART_OF_THE_SEA, Skill.REGENERATOR.material)
        assertEquals(Material.GOLDEN_APPLE, Skill.ABSORBER.material)
        assertEquals(Material.FEATHER, Skill.FEATHERFALL.material)
        assertEquals(Material.SNOWBALL, Skill.SNOW_SPAMMER.material)
        assertEquals(Material.EGG, Skill.LUCKY_BIRD.material)
        assertEquals(Material.BONE, Skill.WOLFLORD.material)
        assertEquals(Material.ENDER_PEARL, Skill.ENDERMASTER.material)
        assertEquals(Material.BREWING_STAND, Skill.WITCH.material)
        assertEquals(Material.STONE_BRICKS, Skill.BUILDER.material)
        assertEquals(Material.IRON_SWORD, Skill.SWORDMASTER.material)
        assertEquals(Material.BOW, Skill.BOWMASTER.material)
        assertEquals(Material.FERMENTED_SPIDER_EYE, Skill.INVISIBLE_STALKER.material)
        assertEquals(Material.BLAZE_POWDER, Skill.STRENGTH_CORE.material)
    }

    @Test
    fun `test all skills have display names`() {
        Skill.values().forEach { skill ->
            assertNotNull(skill.displayName)
            assertTrue(skill.displayName.isNotEmpty())
        }
    }

    @Test
    fun `test all skills have descriptions`() {
        Skill.values().forEach { skill ->
            assertNotNull(skill.description)
            assertTrue(skill.description.isNotEmpty())
        }
    }

    @Test
    fun `test all skills have types`() {
        Skill.values().forEach { skill ->
            assertNotNull(skill.type)
        }
    }

    @Test
    fun `test skill types are correct`() {
        assertEquals(SkillType.SUSTAIN, Skill.JUMBO.type)
        assertEquals(SkillType.SUSTAIN, Skill.REGENERATOR.type)
        assertEquals(SkillType.DEFENSE, Skill.ABSORBER.type)
        assertEquals(SkillType.MOBILITY, Skill.FEATHERFALL.type)
        assertEquals(SkillType.UTILITY, Skill.SNOW_SPAMMER.type)
        assertEquals(SkillType.UTILITY, Skill.LUCKY_BIRD.type)
        assertEquals(SkillType.SUMMONER, Skill.WOLFLORD.type)
        assertEquals(SkillType.MOBILITY, Skill.ENDERMASTER.type)
        assertEquals(SkillType.UTILITY, Skill.WITCH.type)
        assertEquals(SkillType.UTILITY, Skill.BUILDER.type)
        assertEquals(SkillType.COMBAT, Skill.SWORDMASTER.type)
        assertEquals(SkillType.COMBAT, Skill.BOWMASTER.type)
        assertEquals(SkillType.UTILITY, Skill.INVISIBLE_STALKER.type)
        assertEquals(SkillType.COMBAT, Skill.STRENGTH_CORE.type)
    }

    @Test
    fun `test toItemStack creates item with correct material`() {
        val item = Skill.JUMBO.toItemStack(false)
        assertEquals(Material.BREAD, item.type)
    }

    @Test
    fun `test toItemStack has display name`() {
        val item = Skill.JUMBO.toItemStack(false)
        val meta = item.itemMeta
        assertNotNull(meta.displayName())

        val plainText = PlainTextComponentSerializer.plainText().serialize(meta.displayName())
        assertEquals("Jumbo", plainText)
    }

    @Test
    fun `test toItemStack has lore`() {
        val item = Skill.JUMBO.toItemStack(false)
        val meta = item.itemMeta
        assertNotNull(meta.lore())
        assertTrue(meta.lore()!!.isNotEmpty())
    }

    @Test
    fun `test unselected skill has click to select lore`() {
        val item = Skill.JUMBO.toItemStack(false)
        val meta = item.itemMeta
        val lore = meta.lore()!!

        val lastLine = PlainTextComponentSerializer.plainText().serialize(lore.last())
        assertEquals("Click to select", lastLine)
    }

    @Test
    fun `test selected skill has selected lore`() {
        val item = Skill.JUMBO.toItemStack(true)
        val meta = item.itemMeta
        val lore = meta.lore()!!

        // Find the "SELECTED" line
        val selectedLine = lore.find { line ->
            PlainTextComponentSerializer.plainText().serialize(line).contains("SELECTED")
        }
        assertNotNull(selectedLine)
    }

    @Test
    fun `test selected skill has enchantment`() {
        val item = Skill.JUMBO.toItemStack(true)
        val meta = item.itemMeta

        assertTrue(meta.hasEnchant(Enchantment.UNBREAKING))
        assertEquals(1, meta.getEnchantLevel(Enchantment.UNBREAKING))
    }

    @Test
    fun `test selected skill hides enchantments`() {
        val item = Skill.JUMBO.toItemStack(true)
        val meta = item.itemMeta

        assertTrue(meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))
    }

    @Test
    fun `test unselected skill has no enchantment`() {
        val item = Skill.JUMBO.toItemStack(false)
        val meta = item.itemMeta

        assertFalse(meta.hasEnchant(Enchantment.UNBREAKING))
    }

    @Test
    fun `test skill display names match requirements`() {
        assertEquals("Jumbo", Skill.JUMBO.displayName)
        assertEquals("Regenerator", Skill.REGENERATOR.displayName)
        assertEquals("Absorber", Skill.ABSORBER.displayName)
        assertEquals("Featherfall", Skill.FEATHERFALL.displayName)
        assertEquals("Snow Spammer", Skill.SNOW_SPAMMER.displayName)
        assertEquals("Lucky Bird", Skill.LUCKY_BIRD.displayName)
        assertEquals("Wolflord", Skill.WOLFLORD.displayName)
        assertEquals("Endermaster", Skill.ENDERMASTER.displayName)
        assertEquals("Witch", Skill.WITCH.displayName)
        assertEquals("Builder", Skill.BUILDER.displayName)
        assertEquals("Swordmaster", Skill.SWORDMASTER.displayName)
        assertEquals("Bowmaster", Skill.BOWMASTER.displayName)
        assertEquals("Invisible Stalker", Skill.INVISIBLE_STALKER.displayName)
        assertEquals("Strength Core", Skill.STRENGTH_CORE.displayName)
    }
}
