package com.carinaschoppe.skylife.utility.messages

import com.carinaschoppe.skylife.Skylife
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import java.io.File

class MessageLoaderMockBukkitTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `loadMessages creates messages file when missing`() {
        val server = MockBukkit.getMock()!!
        val file = File(server.pluginsFolder, Skylife.folderLocation + "messages.json")
        if (file.exists()) {
            file.delete()
        }

        MessageLoader.loadMessages()

        kotlin.test.assertTrue(file.exists())
    }
}
