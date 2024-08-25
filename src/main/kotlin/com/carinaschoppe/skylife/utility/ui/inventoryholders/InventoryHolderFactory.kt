package com.carinaschoppe.skylife.utility.ui.inventoryholders

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class InventoryHolderFactory(val inventoryName: String, val size: Int) : InventoryHolder {


    abstract val internalInventory: Inventory

    override fun getInventory(): Inventory {
        return this.internalInventory
    }
}