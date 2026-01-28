package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.utility.ui.KitPurchaseConfirmGui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * Listener for kit purchase confirmation GUI interactions.
 * Handles click events and inventory close events for the purchase GUI.
 */
class KitPurchaseGuiListener : Listener {

    /**
     * Handles inventory click events in the kit purchase confirmation GUI.
     * Prevents item movement and delegates click handling to KitPurchaseConfirmGui.
     *
     * @param event The inventory click event
     */
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inventory = event.inventory

        if (!KitPurchaseConfirmGui.isPurchaseGui(inventory)) {
            return
        }

        event.isCancelled = true

        if (event.clickedInventory != inventory) {
            return
        }

        val handled = KitPurchaseConfirmGui.handleClick(player, event.slot)
        if (!handled) {
            // Clicked on other slot, do nothing
        }
    }

    /**
     * Handles inventory close events for the purchase confirmation GUI.
     * Cleans up pending purchase data when the GUI is closed.
     *
     * @param event The inventory close event
     */
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

        if (KitPurchaseConfirmGui.isPurchaseGui(event.inventory)) {
            KitPurchaseConfirmGui.cleanup(player)
        }
    }
}
