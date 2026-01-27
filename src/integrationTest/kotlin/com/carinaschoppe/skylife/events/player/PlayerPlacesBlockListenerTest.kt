package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class PlayerPlacesBlockListenerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        GameClusterTestHelper.reset()
    }

    @AfterEach
    fun tearDown() {
        GameClusterTestHelper.reset()
        MockBukkit.unmock()
    }

    @Test
    fun `block place is cancelled outside ingame`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val location = Location(world, 0.0, 64.0, 0.0)

        val pattern = GamePattern("Map")
        val game = Game(
            name = "Test",
            minPlayers = 1,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "Map",
            pattern = pattern
        )
        GameCluster.addGame(game)

        val player = server.addPlayer()
        game.livingPlayers.add(player)

        val block = world.getBlockAt(0, 64, 0)
        val replaced = block.state
        val placed = block
        val itemInHand = ItemStack(Material.STONE)
        val event = BlockPlaceEvent(placed, replaced, block, itemInHand, player, true, org.bukkit.inventory.EquipmentSlot.HAND)

        PlayerPlacesBlockListener().onBlockPlace(event)

        kotlin.test.assertTrue(event.isCancelled)
    }

    @Test
    fun `block place allowed in ingame`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val location = Location(world, 0.0, 64.0, 0.0)

        val pattern = GamePattern("Map")
        val game = Game(
            name = "Test",
            minPlayers = 1,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "Map",
            pattern = pattern
        )
        game.currentState = IngameState(game)
        GameCluster.addGame(game)

        val player = server.addPlayer()
        game.livingPlayers.add(player)

        val block = world.getBlockAt(0, 64, 0)
        val replaced = block.state
        val placed = block
        val itemInHand = ItemStack(Material.STONE)
        val event = BlockPlaceEvent(placed, replaced, block, itemInHand, player, true, org.bukkit.inventory.EquipmentSlot.HAND)

        PlayerPlacesBlockListener().onBlockPlace(event)

        kotlin.test.assertFalse(event.isCancelled)
    }
}
