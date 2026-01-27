package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import org.bukkit.Bukkit
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class GameClusterTest {

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
    fun `add and remove player updates scoreboard`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val location = Location(world, 0.0, 64.0, 0.0)

        val pattern = GamePattern("TestMap")
        val game = Game(
            name = "Test",
            minPlayers = 2,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "TestMap",
            pattern = pattern
        )
        GameCluster.addGame(game)

        val player = server.addPlayer()
        GameCluster.addPlayerToGame(player, game)

        kotlin.test.assertTrue(game.livingPlayers.contains(player))
        kotlin.test.assertTrue(player.scoreboard.getObjective("skylife") != null)

        GameCluster.removePlayerFromGame(player)

        kotlin.test.assertFalse(game.livingPlayers.contains(player))
        kotlin.test.assertSame(Bukkit.getScoreboardManager().mainScoreboard, player.scoreboard)
    }
}
