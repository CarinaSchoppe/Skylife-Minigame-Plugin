package com.carinaschoppe.skylife.utility.scoreboard

import org.junit.jupiter.api.Test

class ScoreboardTextRendererTest {

    @Test
    fun `renderLines replaces placeholders and makes lines unique`() {
        val templates = listOf(
            "<aqua>{server}</aqua>",
            "<aqua>{server}</aqua>"
        )
        val placeholders = mapOf("{server}" to "Skylife")

        val rendered = ScoreboardTextRenderer.renderLines(templates, placeholders, maxLines = 15)

        kotlin.test.assertEquals(2, rendered.size)
        kotlin.test.assertNotEquals(rendered[0], rendered[1])
        kotlin.test.assertEquals("Skylife", stripLegacy(rendered[0]))
    }

    @Test
    fun `renderLines keeps blank lines visible`() {
        val templates = listOf(" ")
        val rendered = ScoreboardTextRenderer.renderLines(templates, emptyMap(), maxLines = 15)

        kotlin.test.assertTrue(rendered.first().isNotEmpty())
    }

    @Test
    fun `applyPlaceholders replaces tokens`() {
        val result = ScoreboardTextRenderer.applyPlaceholders("Hello {name}", mapOf("{name}" to "Skylife"))

        kotlin.test.assertEquals("Hello Skylife", result)
    }

    private fun stripLegacy(text: String): String {
        return text.replace(Regex("(?i)§[0-9A-FK-OR]"), "")
    }
}
