package com.carinaschoppe.skylife.utility.scoreboard

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.database.DatabaseConnector
import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import java.io.File
import kotlin.test.assertTrue

class ScoreboardManagerMockBukkitTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        DatabaseConnector.connectDatabase()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `scoreboard renders configured server name`() {
        val server = MockBukkit.getMock()!!
        val world = server.addSimpleWorld("world")

        val configFile = File(server.pluginsFolder, Skylife.folderLocation + "config.json")
        configFile.parentFile.mkdirs()
        configFile.writeText(
            """
            {
              "scoreboard": {
                "server_name": "TestServer"
              }
            }
            """.trimIndent()
        )

        ConfigurationLoader.loadConfiguration()

        val location = org.bukkit.Location(world, 0.0, 64.0, 0.0)
        val game = Game(
            name = "Test",
            minPlayers = 1,
            maxPlayers = 4,
            lobbyLocation = location,
            ingameLocation = location,
            mapName = "TestMap",
            pattern = GamePattern("TestMap")
        )

        val player = server.addPlayer()
        game.livingPlayers.add(player)

        ScoreboardManager.setScoreboard(player, game)

        val objective = player.scoreboard.getObjective("skylife")
        assertTrue(objective != null)

        val placeholders = mapOf(
            "{server}" to ConfigurationLoader.config.scoreboard.serverName,
            "{map}" to game.mapName,
            "{alive}" to game.livingPlayers.size.toString(),
            "{max}" to game.maxPlayers.toString(),
            "{kills}" to "0",
            "{kills_total}" to "0",
            "{kit}" to "None",
            "{rank}" to "1",
            "{player}" to player.name,
            "{state}" to game.state.name
        )

        val rendered = ScoreboardTextRenderer.renderLines(
            ConfigurationLoader.config.scoreboard.lines,
            placeholders
        ).map { stripLegacy(it) }

        assertTrue(rendered.any { it.contains("TestServer") })
    }

    private fun stripLegacy(text: String): String {
        return text.replace(Regex("(?i)§[0-9A-FK-OR]"), "").replace("\u200B", "")
    }
}
