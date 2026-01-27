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

class JoinGameCommandTest {

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
    fun `join command adds player to game and sets scoreboard`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val location = Location(world, 0.0, 64.0, 0.0)

        val pattern = GamePattern("TestMap")
        GameCluster.gamePatterns.add(pattern)

        val game = Game(
            name = "TestMap",
            minPlayers = 2,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "TestMap",
            pattern = pattern
        )
        GameCluster.addGame(game)

        val player = server.addPlayer()
        val plugin = MockBukkit.createMockPlugin()
        player.addAttachment(plugin).setPermission("skylife.join", true)
        player.addAttachment(plugin).setPermission("skylife.join.map", true)

        val command = TestCommand("join")
        val handled = JoinGameCommand().onCommand(player, command, "join", arrayOf("TestMap"))

        kotlin.test.assertTrue(handled)
        kotlin.test.assertTrue(game.livingPlayers.contains(player))
        kotlin.test.assertTrue(player.scoreboard.getObjective("skylife") != null)
    }
}
