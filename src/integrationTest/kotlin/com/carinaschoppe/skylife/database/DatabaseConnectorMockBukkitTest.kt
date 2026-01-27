package com.carinaschoppe.skylife.database

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.utility.statistics.StatsPlayers
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import java.io.File

class DatabaseConnectorMockBukkitTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `connectDatabase creates database file and tables`() {
        val server = MockBukkit.getMock()!!
        val dbFile = File(server.pluginsFolder, Skylife.folderLocation + "database.db")
        if (dbFile.exists()) {
            dbFile.delete()
        }

        DatabaseConnector.connectDatabase()

        kotlin.test.assertTrue(dbFile.exists())
        transaction {
            StatsPlayers.selectAll().count()
        }
    }
}
