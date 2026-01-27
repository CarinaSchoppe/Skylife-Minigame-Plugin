package com.carinaschoppe.skylife.utility.configuration

import com.carinaschoppe.skylife.Skylife
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import java.io.File

class ConfigurationMigrationMockBukkitTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `legacy config is migrated and saved`() {
        val pluginsDir = MockBukkit.getMock()!!.pluginsFolder
        val configFile = File(pluginsDir, Skylife.folderLocation + "config.json")
        configFile.parentFile.mkdirs()
        configFile.writeText(
            """
            {
              "scoreboard_title": "&cTitle",
              "scoreboard": {
                "server_name": "&aSkylife",
                "lines": ["&bLine"]
              }
            }
            """.trimIndent()
        )

        ConfigurationLoader.loadConfiguration()

        val migrated = configFile.readText()
        kotlin.test.assertFalse(migrated.contains("&"))
        kotlin.test.assertFalse(migrated.contains("§"))
        kotlin.test.assertTrue(migrated.contains("<"))
    }
}
