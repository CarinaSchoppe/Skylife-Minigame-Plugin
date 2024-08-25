package com.carinaschoppe.skylife.utility.ui.inventoryholders

import com.carinaschoppe.skylife.Skylife
import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class InventoryHolder(inventoryName: String, val size: Int) : InventoryHolder {

    init {
        val inventory = Skylife.instance.server.createInventory(
            this,
            size,
            Component.text(inventoryName)
        )
    }


    override fun getInventory(): Inventory {
        return inventory
    }
}