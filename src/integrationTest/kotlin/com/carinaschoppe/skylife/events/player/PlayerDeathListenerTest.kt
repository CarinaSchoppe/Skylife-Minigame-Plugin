package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.Location
import org.bukkit.event.entity.PlayerDeathEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PlayerDeathListenerTest {

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
    fun `player death moves to spectators`() {
        val server = MockBukkit.getMock()!!
        val templateWorld = server.addSimpleWorld("template")

        val location = Location(templateWorld, 0.0, 64.0, 0.0)
        val pattern = GamePattern("Map")
        pattern.gameLocationManager.spectatorLocation = SkylifeLocation("template", 0.0, 64.0, 0.0, 0f, 0f)
        pattern.gameLocationManager.lobbyLocation = SkylifeLocation("template", 0.0, 64.0, 0.0, 0f, 0f)
        pattern.gameLocationManager.mainLocation = SkylifeLocation("template", 0.0, 64.0, 0.0, 0f, 0f)
        pattern.gameLocationManager.spawnLocations.add(SkylifeLocation("template", 0.0, 64.0, 0.0, 0f, 0f))

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

        server.addSimpleWorld(game.gameID.toString())

        val player = server.addPlayer()
        StatsUtility.addStatsPlayerWhenFirstJoin(player)
        game.livingPlayers.add(player)

        val event = mock<PlayerDeathEvent>()
        whenever(event.player).thenReturn(player)
        PlayerDeathListener().onPlayerDeath(event)

        kotlin.test.assertFalse(game.livingPlayers.contains(player))
        kotlin.test.assertTrue(game.spectators.contains(player))
    }
}
