package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.utility.ui.inventoryholders.InventoryHolderFactory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GUIBuilder(val holder: InventoryHolderFactory) {


    fun build(): Inventory {
        return holder.inventory
    }

    fun setItem(slot: Int, item: ItemStack): GUIBuilder {
        holder.inventory.setItem(slot, item)
        return this
    }

    fun setItem(item: ItemStack, slot: Int): GUIBuilder {
        holder.inventory.setItem(slot, item)
        return this
    }

    fun fillerPanel(): GUIBuilder {
        for (i in 0 until holder.internalInventory.size) {
            if (holder.inventory.getItem(i) == null)
                setItem(i, Items.FILLER_PANEL)
        }
        return this
    }

    fun addItems(items: Map<ItemStack, Int>): GUIBuilder {
        items.forEach { item, slot -> setItem(slot, item) }
        return this
    }

}