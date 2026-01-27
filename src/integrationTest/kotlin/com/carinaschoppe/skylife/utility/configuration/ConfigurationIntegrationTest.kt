package com.carinaschoppe.skylife.utility.configuration

import com.carinaschoppe.skylife.utility.scoreboard.ScoreboardTextRenderer
import org.junit.jupiter.api.Test

class ConfigurationIntegrationTest {

    @Test
    fun `parse config and render scoreboard lines`() {
        val json = """
            {
              "scoreboard_title": "<bold><aqua>Skylife</aqua></bold>",
              "scoreboard": {
                "server_name": "Skylife",
                "lines": [
                  "<aqua>{server}</aqua>",
                  "<aqua>{server}</aqua>"
                ]
              }
            }
        """.trimIndent()

        val config = ConfigurationLoader.parseConfigJson(json)
        kotlin.test.assertEquals("Skylife", config.scoreboard.serverName)

        val rendered = ScoreboardTextRenderer.renderLines(
            config.scoreboard.lines,
            mapOf("{server}" to config.scoreboard.serverName)
        )

        kotlin.test.assertEquals(2, rendered.size)
        kotlin.test.assertNotEquals(rendered[0], rendered[1])
        kotlin.test.assertTrue(rendered[0].isNotBlank())
    }
}
