package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
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
 * Only works in Hub (not during active gameplay).
 */
class PlayerGameOverviewItemListener : Listener {

    @EventHandler(ignoreCancelled = false)  // Listen even if cancelled
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (!GameOverviewItems.isMenuItem(item)) {
            return
        }

        // Trigger on any right-click (air or block)
        if (!event.action.name.contains("RIGHT")) {
            return
        }

        event.isCancelled = true
        val player = event.player

        // Check if player is in a game - compass should only work in Hub
        val game = GameCluster.getGamePlayerIsIn(player)
        if (game != null) {
            // Player is in a game - don't allow opening game overview
            return
        }

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
