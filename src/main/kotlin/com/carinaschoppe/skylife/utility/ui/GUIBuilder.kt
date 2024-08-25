package com.carinaschoppe.skylife.utility.ui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GUIBuilder(inventoryName: String, val size: Int) {

    private var inventory: Inventory = Bukkit.createInventory(null, size, Component.text(inventoryName))

    fun build(): Inventory {
        return inventory
    }

    fun setItem(slot: Int, item: ItemStack): GUIBuilder {
        inventory.setItem(slot, item)
        return this
    }

    fun setItem(item: ItemStack, slot: Int): GUIBuilder {
        inventory.setItem(slot, item)
        return this
    }

    fun fillerPanel(): GUIBuilder {
        for (i in 0 until size) {
            if (inventory.getItem(i) != null)
                setItem(i, Items.FILLER_PANEL)
        }
        return this
    }

    fun addItems(items: Map<ItemStack, Int>): GUIBuilder {
        items.forEach { item, slot -> setItem(slot, item) }
        return this
    }

}