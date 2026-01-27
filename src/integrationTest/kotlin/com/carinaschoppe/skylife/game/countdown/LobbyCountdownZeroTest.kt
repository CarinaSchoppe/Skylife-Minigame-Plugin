package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class LobbyCountdownZeroTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        MockBukkit.load(Skylife::class.java)
        GameClusterTestHelper.reset()
    }

    @AfterEach
    fun tearDown() {
        GameClusterTestHelper.reset()
        MockBukkit.unmock()
    }

    @Test
    fun `countdown reaches zero and starts game`() {
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

        val countdown = LobbyCountdown(game)
        countdown.start()

        val scheduler = server.scheduler as org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock
        scheduler.performTicks(20 * 16L)

        kotlin.test.assertTrue(game.state.name == "INGAME")
        kotlin.test.assertEquals(GameCluster.activeGamesList.first(), game)
    }
}
