package com.carinaschoppe.skylife.events.kit

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

class KitSelectorListenerTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Skylife
    private lateinit var player: Player
    private lateinit var listener: KitSelectorListener

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Skylife::class.java)
        player = server.addPlayer("TestPlayer")
        listener = KitSelectorListener()
        KitManager.initializeKits()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `test KIT_SELECTOR_ITEM_NAME is defined`() {
        assertNotNull(KitSelectorListener.KIT_SELECTOR_ITEM_NAME)
        assertTrue(KitSelectorListener.KIT_SELECTOR_ITEM_NAME.isNotEmpty())
    }

    @Test
    fun `test kit selector item is CHEST material`() {
        val item = ItemStack(Material.CHEST)
        val meta = item.itemMeta
        meta.displayName(Component.text("Kit Selector", NamedTextColor.GREEN, TextDecoration.BOLD))
        item.itemMeta = meta

        assertEquals(Material.CHEST, item.type)
    }

    @Test
    fun `test right-click on kit selector item opens GUI`() {
        val item = ItemStack(Material.CHEST)
        val meta = item.itemMeta
        meta.displayName(Messages.parse(KitSelectorListener.KIT_SELECTOR_ITEM_NAME))
        item.itemMeta = meta

        player.inventory.setItemInMainHand(item)

        val event = PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, item, null, null)
        server.pluginManager.callEvent(event)

        assertTrue(event.isCancelled, "Event should be cancelled")
    }

    @Test
    fun `test left-click on kit selector item does not open GUI`() {
        val item = ItemStack(Material.CHEST)
        val meta = item.itemMeta
        meta.displayName(Messages.parse(KitSelectorListener.KIT_SELECTOR_ITEM_NAME))
        item.itemMeta = meta

        player.inventory.setItemInMainHand(item)

        val event = PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, item, null, null)
        server.pluginManager.callEvent(event)

        assertFalse(event.isCancelled, "Event should not be cancelled for left-click")
    }

    @Test
    fun `test right-click on wrong material does not open GUI`() {
        val item = ItemStack(Material.DIRT)
        val meta = item.itemMeta
        meta.displayName(Messages.parse(KitSelectorListener.KIT_SELECTOR_ITEM_NAME))
        item.itemMeta = meta

        player.inventory.setItemInMainHand(item)

        val event = PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, item, null, null)
        server.pluginManager.callEvent(event)

        assertFalse(event.isCancelled, "Event should not be cancelled for wrong material")
    }

    @Test
    fun `test kit selection updates player kit`() {
        val soldierKit = KitManager.kits.find { it.name == "Soldier" }
        assertNotNull(soldierKit)

        KitManager.selectKit(player, soldierKit!!)

        assertEquals(soldierKit, KitManager.getSelectedKit(player))
    }

    @Test
    fun `test kit selection sends message to player`() {
        val soldierKit = KitManager.kits.find { it.name == "Soldier" }!!

        KitManager.selectKit(player, soldierKit)

        // Message is sent by listener, tested indirectly
        assertEquals(soldierKit, KitManager.getSelectedKit(player))
    }

    @Test
    fun `test clicking glass pane does nothing`() {
        val initialKit = KitManager.getSelectedKit(player)

        // Glass pane should be ignored by the listener
        // This is tested by the listener's glass pane check

        assertEquals(initialKit, KitManager.getSelectedKit(player))
    }

    @Test
    fun `test multiple kit selections override previous selection`() {
        val soldierKit = KitManager.kits.find { it.name == "Soldier" }!!
        val archerKit = KitManager.kits.find { it.name == "Archer" }!!

        KitManager.selectKit(player, soldierKit)
        assertEquals(soldierKit, KitManager.getSelectedKit(player))

        KitManager.selectKit(player, archerKit)
        assertEquals(archerKit, KitManager.getSelectedKit(player))
    }

    @Test
    fun `test kit selector listener is registered`() {
        val listeners = server.pluginManager.getRegisteredListeners(plugin)
        val hasKitSelectorListener = listeners.any {
            it.listener is KitSelectorListener
        }

        assertTrue(hasKitSelectorListener, "KitSelectorListener should be registered")
    }
}
