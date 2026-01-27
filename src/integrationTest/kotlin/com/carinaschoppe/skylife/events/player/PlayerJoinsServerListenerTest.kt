package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.utility.statistics.StatsPlayer
import com.carinaschoppe.skylife.utility.statistics.StatsPlayers
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class PlayerJoinsServerListenerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        DatabaseConnector.connectDatabase()
        StatsUtility.statsPlayers.clear()
        transaction { StatsPlayers.deleteAll() }
    }

    @AfterEach
    fun tearDown() {
        StatsUtility.statsPlayers.clear()
        MockBukkit.unmock()
    }

    @Test
    fun `player join resets state and creates stats`() {
        val server = MockBukkit.getMock()!!
        val player = server.addPlayer()

        val event = PlayerJoinEvent(player, "")
        PlayerJoinsServerListener().onPlayerJoin(event)

        kotlin.test.assertEquals(GameMode.ADVENTURE, player.gameMode)
        kotlin.test.assertEquals(20.0, player.health)
        kotlin.test.assertEquals(20, player.foodLevel)
        kotlin.test.assertTrue(player.inventory.contents.all { it == null })

        transaction {
            val stats = StatsPlayer.find { StatsPlayers.uuid eq player.uniqueId.toString() }.toList()
            kotlin.test.assertEquals(1, stats.size)
        }
    }
}
