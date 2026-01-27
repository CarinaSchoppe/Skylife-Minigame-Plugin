package com.carinaschoppe.skylife.game

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GameBroadcastTest {

    @Test
    fun `broadcast sends message to living players and spectators`() {
        val pattern = GamePattern("TestMap")
        val world: World = mock()
        val location = Location(world, 0.0, 0.0, 0.0)
        val game = Game(
            name = "Test",
            minPlayers = 0,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "TestMap",
            pattern = pattern
        )

        val living: Player = mock()
        val spectator: Player = mock()
        game.livingPlayers.add(living)
        game.spectators.add(spectator)

        game.broadcast("<red>Hello</red>")

        verify(living).sendMessage(any<Component>())
        verify(spectator).sendMessage(any<Component>())
    }
}
