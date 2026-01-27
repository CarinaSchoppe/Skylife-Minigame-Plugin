package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolderFactory
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameOverviewPaginationTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        MockBukkit.load(com.carinaschoppe.skylife.Skylife::class.java)
        GameClusterTestHelper.reset()
    }

    @AfterEach
    fun tearDown() {
        GameClusterTestHelper.reset()
        MockBukkit.unmock()
    }

    @Test
    fun `pagination shows next page arrow when many games exist`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val loc = Location(world, 0.0, 0.0, 0.0)

        // Create 53 games (more than 52 slots)
        repeat(53) { i ->
            val pattern = GamePattern("Map$i")
            val game = Game("Game$i", 2, 4, loc, loc, "Map$i", pattern)
            GameCluster.addGame(game)
        }

        val inventory = GUIs.levelSelectInventory(0)

        // Slot 53 should have "Nächste Seite"
        val nextItem = inventory.getItem(53)
        assertNotNull(nextItem)
        assertEquals(Material.ARROW, nextItem.type)
        assertTrue(nextItem.itemMeta.displayName()?.toString()?.contains("Nächste Seite") == true)

        // Slot 45 should be filler or empty (no previous page on page 0)
        val prevItem = inventory.getItem(45)
        assertTrue(prevItem == null || prevItem.type == Material.GRAY_STAINED_GLASS_PANE)
    }

    @Test
    fun `clicking next page opens second page`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val loc = Location(world, 0.0, 0.0, 0.0)
        val player = server.addPlayer()

        repeat(53) { i ->
            val pattern = GamePattern("Map$i")
            val game = Game("Game$i", 2, 4, loc, loc, "Map$i", pattern)
            GameCluster.addGame(game)
        }

        val inventory = GUIs.levelSelectInventory(0)
        player.openInventory(inventory)

        val clickEvent = InventoryClickEvent(
            player.openInventory,
            InventoryType.SlotType.CONTAINER,
            53, // Next page slot
            org.bukkit.event.inventory.ClickType.LEFT,
            org.bukkit.event.inventory.InventoryAction.PICKUP_ALL
        )

        com.carinaschoppe.skylife.events.player.PlayerSelectGameListener().onInventoryClick(clickEvent)

        val topInv = player.openInventory.topInventory
        val holder = topInv.holder as GameOverviewHolderFactory
        assertEquals(1, holder.page)

        // On page 1, slot 45 should be "Vorherige Seite"
        val prevItem = topInv.getItem(45)
        assertNotNull(prevItem)
        assertEquals(Material.ARROW, prevItem.type)
        assertTrue(prevItem.itemMeta.displayName()?.toString()?.contains("Vorherige Seite") == true)
    }

    @Test
    fun `clicking game item joins the game`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val loc = Location(world, 0.0, 0.0, 0.0)
        val player = server.addPlayer()

        val pattern = GamePattern("TestMap")
        val game = Game("TestGame", 2, 4, loc, loc, "TestMap", pattern)
        GameCluster.addGame(game)

        val inventory = GUIs.levelSelectInventory(0)
        player.openInventory(inventory)

        // Game should be in slot 0
        val clickEvent = InventoryClickEvent(
            player.openInventory,
            InventoryType.SlotType.CONTAINER,
            0,
            org.bukkit.event.inventory.ClickType.LEFT,
            org.bukkit.event.inventory.InventoryAction.PICKUP_ALL
        )

        com.carinaschoppe.skylife.events.player.PlayerSelectGameListener().onInventoryClick(clickEvent)

        assertTrue(game.livingPlayers.contains(player))
        // Inventory should be closed after joining
        // MockBukkit behavior for closeInventory might vary, but we can check if player is in game
    }
}
