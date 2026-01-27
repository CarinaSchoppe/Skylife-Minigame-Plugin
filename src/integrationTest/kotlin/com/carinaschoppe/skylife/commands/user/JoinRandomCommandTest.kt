package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import com.carinaschoppe.skylife.testutil.TestCommand
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class JoinRandomCommandTest {

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
    fun `join random adds player to available game`() {
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
        val plugin = MockBukkit.createMockPlugin()
        player.addAttachment(plugin).setPermission("skylife.join", true)
        player.addAttachment(plugin).setPermission("skylife.join.random", true)

        val command = TestCommand("join")
        val handled = JoinGameCommand().onCommand(player, command, "join", arrayOf("random"))

        kotlin.test.assertTrue(handled)
        kotlin.test.assertTrue(game.livingPlayers.contains(player))
    }
}
