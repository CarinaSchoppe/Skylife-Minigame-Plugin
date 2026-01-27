package com.carinaschoppe.skylife.utility.statistics

import com.carinaschoppe.skylife.database.DatabaseConnector
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class StatsUtilityMockBukkitTest {

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
    fun `addKillStatsToPlayer increments kills and points`() {
        val server = MockBukkit.getMock()!!
        val player = server.addPlayer()

        StatsUtility.addStatsPlayerWhenFirstJoin(player)
        StatsUtility.addKillStatsToPlayer(player)

        transaction {
            val stats = StatsPlayer.find { StatsPlayers.uuid eq player.uniqueId.toString() }.first()
            kotlin.test.assertEquals(1, stats.kills)
            kotlin.test.assertEquals(1, stats.points)
        }
    }

    @Test
    fun `getPlayerRank orders by points`() {
        val server = MockBukkit.getMock()!!
        val playerA = server.addPlayer()
        val playerB = server.addPlayer()

        StatsUtility.addStatsPlayerWhenFirstJoin(playerA)
        StatsUtility.addStatsPlayerWhenFirstJoin(playerB)

        transaction {
            val statsA = StatsPlayer.find { StatsPlayers.uuid eq playerA.uniqueId.toString() }.first()
            val statsB = StatsPlayer.find { StatsPlayers.uuid eq playerB.uniqueId.toString() }.first()
            statsA.points = 5
            statsB.points = 10
        }

        val rankB = StatsUtility.getPlayerRank(playerB)
        val rankA = StatsUtility.getPlayerRank(playerA)

        kotlin.test.assertEquals(1, rankB)
        kotlin.test.assertEquals(2, rankA)
    }
}
