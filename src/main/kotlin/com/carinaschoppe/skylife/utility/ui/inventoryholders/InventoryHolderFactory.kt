package com.carinaschoppe.skylife.utility.ui.inventoryholders

import com.carinaschoppe.skylife.Skylife
import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class InventoryHolderFactory(val inventoryName: String, val size: Int) : InventoryHolder {

    lateinit var internalInventory: Inventory

    fun initInventory(): InventoryHolderFactory {
        internalInventory = Skylife.instance.server.createInventory(this, size, Component.text(inventoryName))
        return this
    }

    override fun getInventory(): Inventory {
        return this.internalInventory
    }
}