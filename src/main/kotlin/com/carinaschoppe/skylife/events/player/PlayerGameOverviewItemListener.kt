package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GUIs
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Handles interactions with the game overview menu item.
 */
class PlayerGameOverviewItemListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (!GameOverviewItems.isMenuItem(item)) {
            return
        }

        // Trigger on any right-click, not just on blocks
        if (!event.action.name.contains("RIGHT")) {
            return
        }

        event.isCancelled = true
        val player = event.player
        if (!player.hasPermission("skylife.overview")) {
            player.sendMessage(Messages.ERROR_PERMISSION)
            return
        }
        player.openInventory(GUIs.levelSelectInventory())
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (GameOverviewItems.isMenuItem(event.itemDrop.itemStack)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val currentItem = event.currentItem
        val cursor = event.cursor
        if (GameOverviewItems.isMenuItem(currentItem) || GameOverviewItems.isMenuItem(cursor)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (GameOverviewItems.isMenuItem(event.oldCursor)) {
            event.isCancelled = true
        }
    }
}
