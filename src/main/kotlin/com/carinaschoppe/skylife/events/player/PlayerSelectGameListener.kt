package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolderFactory
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerSelectGameListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.clickedInventory?.getHolder(false) !is GameOverviewHolderFactory) {
            return
        }

        event.isCancelled = true
        //get name of clicked item
        val item = event.currentItem ?: return
        val name = PlainTextComponentSerializer.plainText().serialize(item.itemMeta?.displayName()!!)
        if (name == "") {
            return
        }
        if (event.whoClicked !is Player)
            return

        (event.whoClicked as Player).performCommand("join $name")
    }


}