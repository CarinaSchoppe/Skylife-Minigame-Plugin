package com.carinaschoppe.skylife.utility.configuration

import org.junit.jupiter.api.Test

class ConfigurationLoaderTest {

    @Test
    fun `parse config uses scoreboard_title when scoreboard title missing`() {
        val json = """
            {
              "scoreboard_title": "<red>Title</red>"
            }
        """.trimIndent()

        val config = ConfigurationLoader.parseConfigJson(json)

        kotlin.test.assertEquals("<red>Title</red>", config.scoreboardTitle)
        kotlin.test.assertEquals("<red>Title</red>", config.scoreboard.title)
    }

    @Test
    fun `parse config keeps default scoreboard lines when missing`() {
        val json = """
            {
              "scoreboard": {
                "server_name": "Skylife"
              }
            }
        """.trimIndent()

        val config = ConfigurationLoader.parseConfigJson(json)

        kotlin.test.assertTrue(config.scoreboard.lines.isNotEmpty())
    }

    @Test
    fun `legacy scoreboard title is migrated to MiniMessage`() {
        val json = """
            {
              "scoreboard_title": "&cTitle"
            }
        """.trimIndent()

        val config = ConfigurationLoader.parseConfigJson(json)

        kotlin.test.assertEquals(
            "Title", net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(com.carinaschoppe.skylife.utility.messages.Messages.parse(config.scoreboardTitle))
        )
        kotlin.test.assertTrue(config.scoreboardTitle.contains("<") || !config.scoreboardTitle.contains("&"))
    }
}
