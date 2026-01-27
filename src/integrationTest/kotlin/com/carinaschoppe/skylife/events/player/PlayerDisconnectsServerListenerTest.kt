package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import com.carinaschoppe.skylife.utility.statistics.StatsPlayers
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.Location
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class PlayerDisconnectsServerListenerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        DatabaseConnector.connectDatabase()
        StatsUtility.statsPlayers.clear()
        transaction { StatsPlayers.deleteAll() }
        GameClusterTestHelper.reset()
    }

    @AfterEach
    fun tearDown() {
        StatsUtility.statsPlayers.clear()
        GameClusterTestHelper.reset()
        MockBukkit.unmock()
    }

    @Test
    fun `player quit removes from game and resets scoreboard`() {
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
        StatsUtility.addStatsPlayerWhenFirstJoin(player)
        GameCluster.addPlayerToGame(player, game)

        val event = PlayerQuitEvent(player, "")
        PlayerDisconnectsServerListener().onPlayerQuit(event)

        kotlin.test.assertFalse(game.livingPlayers.contains(player))
        kotlin.test.assertSame(org.bukkit.Bukkit.getScoreboardManager().mainScoreboard, player.scoreboard)
    }
}
