package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GamePattern
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class MapManagerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `locationWorldConverter moves location to game world`() {
        val server = MockBukkit.getMock()!!
        val templateWorld = server.addSimpleWorld("template")

        val pattern = GamePattern("Map")
        val location = Location(templateWorld, 1.0, 2.0, 3.0)
        val game = Game(
            name = "Test",
            minPlayers = 1,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "Map",
            pattern = pattern
        )

        server.addSimpleWorld(game.gameID.toString())

        val converted = MapManager.locationWorldConverter(location, game)

        kotlin.test.assertEquals(game.gameID.toString(), converted.world?.name)
        kotlin.test.assertEquals(1.0, converted.x)
        kotlin.test.assertEquals(2.0, converted.y)
        kotlin.test.assertEquals(3.0, converted.z)
    }
}
