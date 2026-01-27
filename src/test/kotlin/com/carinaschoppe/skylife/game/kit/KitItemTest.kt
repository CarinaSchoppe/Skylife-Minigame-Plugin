package com.carinaschoppe.skylife.game.kit

import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class KitItemTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `toItemStack applies MiniMessage name and lore`() {
        val lore = listOf(Messages.parse("<gray>Line</gray>"))
        val kitItem = KitItem(Material.DIAMOND, name = "<red>Gem</red>", lore = lore)

        val itemStack = kitItem.toItemStack()
        val meta = itemStack.itemMeta

        val name = meta.displayName()
        org.junit.jupiter.api.Assertions.assertNotNull(name)
        val plainName = PlainTextComponentSerializer.plainText().serialize(name!!)

        kotlin.test.assertEquals("Gem", plainName)
        org.junit.jupiter.api.Assertions.assertNotNull(meta.lore())
        kotlin.test.assertEquals(1, meta.lore()!!.size)
    }
}
