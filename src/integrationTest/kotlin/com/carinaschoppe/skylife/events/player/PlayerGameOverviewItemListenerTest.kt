package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
import org.bukkit.event.player.PlayerDropItemEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import kotlin.test.assertTrue

class PlayerGameOverviewItemListenerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        MockBukkit.load(com.carinaschoppe.skylife.Skylife::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `dropping menu item is cancelled`() {
        val server = MockBukkit.getMock()!!
        val player = server.addPlayer()
        val item = GameOverviewItems.createMenuItem()

        val itemDrop = player.world.dropItem(player.location, item)
        val event = PlayerDropItemEvent(player, itemDrop)

        PlayerGameOverviewItemListener().onPlayerDropItem(event)

        assertTrue(event.isCancelled)
    }
}
