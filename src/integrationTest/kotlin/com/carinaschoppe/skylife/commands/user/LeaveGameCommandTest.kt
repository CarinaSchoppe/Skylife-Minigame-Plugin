package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import com.carinaschoppe.skylife.testutil.TestCommand
import com.carinaschoppe.skylife.utility.statistics.StatsPlayers
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.Location
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class LeaveGameCommandTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        GameClusterTestHelper.reset()
        DatabaseConnector.connectDatabase()
        StatsUtility.statsPlayers.clear()
        transaction { StatsPlayers.deleteAll() }
    }

    @AfterEach
    fun tearDown() {
        StatsUtility.statsPlayers.clear()
        GameClusterTestHelper.reset()
        MockBukkit.unmock()
    }

    @Test
    fun `leave command removes player from game`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val location = Location(world, 0.0, 64.0, 0.0)

        val pattern = GamePattern("TestMap")
        GameCluster.gamePatterns.add(pattern)

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
        StatsUtility.addStatsPlayerWhenFirstJoin(player)
        GameCluster.addPlayerToGame(player, game)

        val plugin = MockBukkit.createMockPlugin()
        player.addAttachment(plugin).setPermission("skylife.leave", true)

        val command = TestCommand("leave")
        val handled = LeaveGameCommand().onCommand(player, command, "leave", emptyArray())

        kotlin.test.assertTrue(handled)
        kotlin.test.assertFalse(game.livingPlayers.contains(player))
    }
}
