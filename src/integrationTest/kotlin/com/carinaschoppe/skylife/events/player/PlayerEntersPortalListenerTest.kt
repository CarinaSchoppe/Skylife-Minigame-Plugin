package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.player.PlayerMoveEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class PlayerEntersPortalListenerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        DatabaseConnector.connectDatabase()
        GameClusterTestHelper.reset()
    }

    @AfterEach
    fun tearDown() {
        GameClusterTestHelper.reset()
        MockBukkit.unmock()
    }

    @Test
    fun `player stepping on end portal joins random game`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val location = Location(world, 0.0, 64.0, 0.0)

        val pattern = GamePattern("Map")
        val game = Game(
            name = "Test",
            minPlayers = 2,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "Map",
            pattern = pattern
        )
        GameCluster.addGame(game)

        val player = server.addPlayer()

        val from = Location(world, 0.0, 64.0, 0.0)
        val to = Location(world, 1.0, 64.0, 0.0)
        to.block.type = Material.END_PORTAL

        val event = PlayerMoveEvent(player, from, to)
        PlayerEntersPortalListener().onPlayerMove(event)

        kotlin.test.assertTrue(game.livingPlayers.contains(player))
    }
}
