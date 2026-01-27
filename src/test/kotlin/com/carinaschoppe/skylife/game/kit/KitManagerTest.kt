package com.carinaschoppe.skylife.game.kit

import com.carinaschoppe.skylife.Skylife
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

class KitManagerTest {

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
    fun `test initializeKits creates kits`() {
        KitManager.initializeKits()

        assertTrue(KitManager.kits.isNotEmpty(), "Kits should be initialized")
    }

    @Test
    fun `test initializeKits creates Soldier kit`() {
        KitManager.initializeKits()

        val soldierKit = KitManager.kits.find { it.name == "Soldier" }
        assertNotNull(soldierKit, "Soldier kit should exist")
    }

    @Test
    fun `test initializeKits creates Archer kit`() {
        KitManager.initializeKits()

        val archerKit = KitManager.kits.find { it.name == "Archer" }
        assertNotNull(archerKit, "Archer kit should exist")
    }

    @Test
    fun `test Soldier kit has correct icon material`() {
        KitManager.initializeKits()

        val soldierKit = KitManager.kits.find { it.name == "Soldier" }
        assertEquals(Material.DIAMOND_SWORD, soldierKit!!.icon.material)
    }

    @Test
    fun `test Archer kit has correct icon material`() {
        KitManager.initializeKits()

        val archerKit = KitManager.kits.find { it.name == "Archer" }
        assertEquals(Material.BOW, archerKit!!.icon.material)
    }

    @Test
    fun `test Soldier kit has diamond sword with sharpness`() {
        KitManager.initializeKits()

        val soldierKit = KitManager.kits.find { it.name == "Soldier" }!!
        val sword = soldierKit.items.find { it.material == Material.DIAMOND_SWORD }

        assertNotNull(sword, "Soldier kit should have diamond sword")
        assertTrue(sword!!.enchantments.containsKey(Enchantment.SHARPNESS))
        assertEquals(1, sword.enchantments[Enchantment.SHARPNESS])
    }

    @Test
    fun `test Soldier kit has full iron armor`() {
        KitManager.initializeKits()

        val soldierKit = KitManager.kits.find { it.name == "Soldier" }!!

        assertTrue(soldierKit.items.any { it.material == Material.IRON_HELMET }, "Should have iron helmet")
        assertTrue(soldierKit.items.any { it.material == Material.IRON_CHESTPLATE }, "Should have iron chestplate")
        assertTrue(soldierKit.items.any { it.material == Material.IRON_LEGGINGS }, "Should have iron leggings")
        assertTrue(soldierKit.items.any { it.material == Material.IRON_BOOTS }, "Should have iron boots")
    }

    @Test
    fun `test Archer kit has bow with power enchantment`() {
        KitManager.initializeKits()

        val archerKit = KitManager.kits.find { it.name == "Archer" }!!
        val bow = archerKit.items.find { it.material == Material.BOW }

        assertNotNull(bow, "Archer kit should have bow")
        assertTrue(bow!!.enchantments.containsKey(Enchantment.POWER))
        assertEquals(1, bow.enchantments[Enchantment.POWER])
    }

    @Test
    fun `test Archer kit has 32 arrows`() {
        KitManager.initializeKits()

        val archerKit = KitManager.kits.find { it.name == "Archer" }!!
        val arrows = archerKit.items.find { it.material == Material.ARROW }

        assertNotNull(arrows, "Archer kit should have arrows")
        assertEquals(32, arrows!!.amount)
    }

    @Test
    fun `test Archer kit has full leather armor`() {
        KitManager.initializeKits()

        val archerKit = KitManager.kits.find { it.name == "Archer" }!!

        assertTrue(archerKit.items.any { it.material == Material.LEATHER_HELMET }, "Should have leather helmet")
        assertTrue(archerKit.items.any { it.material == Material.LEATHER_CHESTPLATE }, "Should have leather chestplate")
        assertTrue(archerKit.items.any { it.material == Material.LEATHER_LEGGINGS }, "Should have leather leggings")
        assertTrue(archerKit.items.any { it.material == Material.LEATHER_BOOTS }, "Should have leather boots")
    }

    @Test
    fun `test selectKit assigns kit to player`() {
        KitManager.initializeKits()
        val kit = KitManager.kits.first()

        KitManager.selectKit(player, kit)

        assertEquals(kit, KitManager.getSelectedKit(player))
    }

    @Test
    fun `test getSelectedKit returns null for player without kit`() {
        val selectedKit = KitManager.getSelectedKit(player)

        assertNull(selectedKit)
    }

    @Test
    fun `test selectKit can change selected kit`() {
        KitManager.initializeKits()
        val soldierKit = KitManager.kits.find { it.name == "Soldier" }!!
        val archerKit = KitManager.kits.find { it.name == "Archer" }!!

        KitManager.selectKit(player, soldierKit)
        assertEquals(soldierKit, KitManager.getSelectedKit(player))

        KitManager.selectKit(player, archerKit)
        assertEquals(archerKit, KitManager.getSelectedKit(player))
    }

    @Test
    fun `test giveKitItems clears inventory`() {
        KitManager.initializeKits()
        val kit = KitManager.kits.first()
        KitManager.selectKit(player, kit)

        player.inventory.addItem(org.bukkit.inventory.ItemStack(Material.DIRT, 64))

        KitManager.giveKitItems(player)

        assertFalse(player.inventory.contains(Material.DIRT), "Inventory should be cleared")
    }

    @Test
    fun `test giveKitItems gives all kit items`() {
        KitManager.initializeKits()
        val soldierKit = KitManager.kits.find { it.name == "Soldier" }!!
        KitManager.selectKit(player, soldierKit)

        KitManager.giveKitItems(player)

        assertTrue(player.inventory.contains(Material.DIAMOND_SWORD), "Should have diamond sword")
        assertTrue(player.inventory.contains(Material.IRON_HELMET), "Should have iron helmet")
        assertTrue(player.inventory.contains(Material.IRON_CHESTPLATE), "Should have iron chestplate")
        assertTrue(player.inventory.contains(Material.IRON_LEGGINGS), "Should have iron leggings")
        assertTrue(player.inventory.contains(Material.IRON_BOOTS), "Should have iron boots")
    }

    @Test
    fun `test giveKitItems does nothing if no kit selected`() {
        player.inventory.addItem(org.bukkit.inventory.ItemStack(Material.DIRT, 64))

        KitManager.giveKitItems(player)

        assertTrue(player.inventory.contains(Material.DIRT), "Inventory should remain unchanged")
    }

    @Test
    fun `test removePlayer removes kit selection`() {
        KitManager.initializeKits()
        val kit = KitManager.kits.first()
        KitManager.selectKit(player, kit)

        KitManager.removePlayer(player)

        assertNull(KitManager.getSelectedKit(player))
    }

    @Test
    fun `test multiple players can have different kits`() {
        KitManager.initializeKits()
        val player1 = server.addPlayer("Player1")
        val player2 = server.addPlayer("Player2")

        val soldierKit = KitManager.kits.find { it.name == "Soldier" }!!
        val archerKit = KitManager.kits.find { it.name == "Archer" }!!

        KitManager.selectKit(player1, soldierKit)
        KitManager.selectKit(player2, archerKit)

        assertEquals(soldierKit, KitManager.getSelectedKit(player1))
        assertEquals(archerKit, KitManager.getSelectedKit(player2))
    }

    @Test
    fun `test initializeKits clears existing kits`() {
        KitManager.initializeKits()
        val firstCount = KitManager.kits.size

        KitManager.initializeKits()
        val secondCount = KitManager.kits.size

        assertEquals(firstCount, secondCount, "Should not duplicate kits on reload")
    }

    @Test
    fun `test kit icons have custom names`() {
        KitManager.initializeKits()

        KitManager.kits.forEach { kit ->
            assertNotNull(kit.icon.name, "Kit icon should have custom name")
        }
    }
}
