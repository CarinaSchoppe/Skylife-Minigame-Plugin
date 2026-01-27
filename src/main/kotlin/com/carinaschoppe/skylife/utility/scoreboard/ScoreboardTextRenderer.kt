package com.carinaschoppe.skylife.utility.scoreboard

import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ScoreboardTextRenderer {

    private const val ZERO_WIDTH_SPACE = '\u200B'
    private val scoreboardSerializer = LegacyComponentSerializer.legacySection()

    fun renderLines(templateLines: List<String>, placeholders: Map<String, String>, maxLines: Int = 15): List<String> {
        val lines = templateLines
            .map { line -> applyPlaceholders(line, placeholders) }
            .map { line -> renderLine(line) }

        return makeUnique(lines).take(maxLines)
    }

    fun applyPlaceholders(text: String, placeholders: Map<String, String>): String {
        var result = text
        placeholders.forEach { (key, value) ->
            result = result.replace(key, value)
        }
        return result
    }

    fun renderLine(text: String): String {
        val serialized = scoreboardSerializer.serialize(Messages.parse(text))
        return if (serialized.isBlank()) " " else serialized
    }

    fun makeUnique(lines: List<String>): List<String> {
        val used = mutableSetOf<String>()
        return lines.map { line ->
            var uniqueLine = line
            while (!used.add(uniqueLine)) {
                uniqueLine += ZERO_WIDTH_SPACE
            }
            uniqueLine
        }
    }
}
