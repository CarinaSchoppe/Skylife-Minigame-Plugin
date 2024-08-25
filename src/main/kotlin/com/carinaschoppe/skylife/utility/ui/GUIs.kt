package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolder
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object GUIs {
    val LEVEL_SELECT_INVENTORY = fun(): Inventory {

        val map = mutableMapOf<ItemStack, Int>()

        val rows = 9
        val cols = 4


        // Sample list of items to place into the grid
        val items = listOf(Items.LEVEL_PADERBORN)

        var itemIndex = 0

// Iterate over the entire grid, but place items only in the inner part
        for (index in 0 until (rows * cols)) {
            val row = index / cols
            val col = index % cols

            // Skip the first and last rows and columns
            if (row in 1 until (rows - 1) && col in 1 until (cols - 1) && itemIndex < items.size) {
                map[items[itemIndex]] = index
                itemIndex++
            }
        }

        return GUIBuilder(GameOverviewHolder())
            .addItems(map)
            .fillerPanel()
            .build()
    }
}