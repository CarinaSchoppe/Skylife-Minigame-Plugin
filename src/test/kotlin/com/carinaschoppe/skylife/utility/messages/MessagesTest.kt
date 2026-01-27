package com.carinaschoppe.skylife.utility.messages

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.junit.jupiter.api.Test

class MessagesTest {

    @Test
    fun `parse handles MiniMessage colors`() {
        val component = Messages.parse("<red>Hello</red>")
        val plain = PlainTextComponentSerializer.plainText().serialize(component)

        kotlin.test.assertEquals("Hello", plain)
        val resolvedColor = component.color() ?: component.children().firstOrNull()?.color()
        kotlin.test.assertEquals(NamedTextColor.RED, resolvedColor)
    }

    @Test
    fun `parse handles plain text`() {
        val component = Messages.parse("Just text")
        val plain = PlainTextComponentSerializer.plainText().serialize(component)

        kotlin.test.assertEquals("Just text", plain)
    }
}
