package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.testutil.GameClusterTestHelper
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class PlayerDamagesListenerTest {

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
    fun `damage cancelled when players not in game`() {
        val server = MockBukkit.getMock()!!
        val attacker = server.addPlayer()
        val victim = server.addPlayer()

        val event = EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0)
        PlayerDamagesListener().onEntityDamageByEntity(event)

        kotlin.test.assertTrue(event.isCancelled)
    }

    @Test
    fun `damage allowed for same game ingame`() {
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

        val attacker: Player = server.addPlayer()
        val victim: Player = server.addPlayer()
        game.livingPlayers.add(attacker)
        game.livingPlayers.add(victim)

        val event = EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0)
        PlayerDamagesListener().onEntityDamageByEntity(event)

        kotlin.test.assertFalse(event.isCancelled)
    }

    @Test
    fun `damage cancelled when players in different games`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")
        val location = Location(world, 0.0, 64.0, 0.0)

        val patternA = GamePattern("MapA")
        val gameA = Game(
            name = "A",
            minPlayers = 1,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "MapA",
            pattern = patternA
        )
        gameA.currentState = IngameState(gameA)
        GameCluster.addGame(gameA)

        val patternB = GamePattern("MapB")
        val gameB = Game(
            name = "B",
            minPlayers = 1,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "MapB",
            pattern = patternB
        )
        gameB.currentState = IngameState(gameB)
        GameCluster.addGame(gameB)

        val attacker = server.addPlayer()
        val victim = server.addPlayer()
        gameA.livingPlayers.add(attacker)
        gameB.livingPlayers.add(victim)

        val event = EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0)
        PlayerDamagesListener().onEntityDamageByEntity(event)

        kotlin.test.assertTrue(event.isCancelled)
    }
}
