package com.carinaschoppe.skylife.utility.ui.inventoryholders

import com.carinaschoppe.skylife.Skylife
import org.bukkit.inventory.Inventory

class GameOverviewHolderFactory : InventoryHolderFactory("Game Overview", 36) {

    override val internalInventory: Inventory = Skylife.instance.server.createInventory(this, 48)

}