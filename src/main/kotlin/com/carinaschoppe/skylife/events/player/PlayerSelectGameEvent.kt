package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerSelectGameEvent : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.clickedInventory?.getHolder(false) !is GameOverviewHolder) {
            return
        }

        event.isCancelled = true
        //get name of clicked item
        val item = event.currentItem ?: return
        val name = item.itemMeta?.displayName()?.examinableName()
        if (name == "") {
            return
        }
        if (event.whoClicked !is Player)
            return

        (event.whoClicked as Player).performCommand("join $name")
    }


}