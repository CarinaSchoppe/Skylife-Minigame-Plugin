package com.carinaschoppe.skylife.game.countdown

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GamePattern
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class LobbyCountdownTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        MockBukkit.load(Skylife::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `countdown stops when players drop below minimum`() {
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

        val countdown = LobbyCountdown(game)
        val player = server.addPlayer()
        game.livingPlayers.add(player)

        countdown.start()
        kotlin.test.assertTrue(countdown.isRunning)

        game.livingPlayers.clear()
        val scheduler = server.scheduler as org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock
        scheduler.performTicks(20)

        kotlin.test.assertFalse(countdown.isRunning)
        kotlin.test.assertEquals(15, countdown.seconds)
    }
}
