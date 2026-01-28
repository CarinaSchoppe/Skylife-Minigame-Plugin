package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.utility.ui.KitPurchaseConfirmGui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * Listener for kit purchase confirmation GUI interactions.
 */
class KitPurchaseGuiListener : Listener {

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

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

        if (KitPurchaseConfirmGui.isPurchaseGui(event.inventory)) {
            KitPurchaseConfirmGui.cleanup(player)
        }
    }
}
