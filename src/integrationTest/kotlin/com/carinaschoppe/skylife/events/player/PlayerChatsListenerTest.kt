package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PlayerChatsListenerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        GameClusterTestHelper.reset()
    }

    @AfterEach
    fun tearDown() {
        GameClusterTestHelper.reset()
        MockBukkit.unmock()
    }

    @Test
    fun `hub chat only reaches hub players`() {
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

        val hubPlayer = server.addPlayer()
        val gamePlayer = server.addPlayer()
        game.livingPlayers.add(gamePlayer)

        val event = mock<AsyncChatEvent>()
        whenever(event.player).thenReturn(hubPlayer)
        whenever(event.message()).thenReturn(Component.text("Hi"))
        PlayerChatsListener().onAsyncChat(event)

        verify(event).isCancelled = true
        kotlin.test.assertTrue(hubPlayer.nextMessage() != null)
        kotlin.test.assertTrue(gamePlayer.nextMessage() == null)
    }

    @Test
    fun `game chat reaches game players`() {
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
        game.currentState = IngameState(game)
        GameCluster.addGame(game)

        val playerA = server.addPlayer()
        val playerB = server.addPlayer()
        game.livingPlayers.add(playerA)
        game.livingPlayers.add(playerB)

        val event = mock<AsyncChatEvent>()
        whenever(event.player).thenReturn(playerA)
        whenever(event.message()).thenReturn(Component.text("Fight"))
        PlayerChatsListener().onAsyncChat(event)

        verify(event).isCancelled = true
        kotlin.test.assertTrue(playerB.nextMessage() != null)
    }
}
