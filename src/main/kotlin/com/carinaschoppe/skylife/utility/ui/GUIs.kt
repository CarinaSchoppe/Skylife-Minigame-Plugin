package com.carinaschoppe.skylife.utility.ui

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object GUIs {
    val LEVEL_SELECT_INVENTORY = fun(): Inventory {

        val map = mutableMapOf<ItemStack, Int>()

        val rows = 9
        val cols = 4

        // Create a grid initialized with empty slots (null or any placeholder)

        // Sample list of items to place into the grid
        val items = listOf("Item1", "Item2", "Item3", "Item4", "Item5", "Item6")

        var itemIndex = 0

// Iterate over the entire grid, but place items only in the inner part
        for (index in 0 until (rows * cols)) {
            val row = index / cols
            val col = index % cols

            // Skip the first and last rows and columns
            if (row in 1 until (rows - 1) && col in 1 until (cols - 1)) {
                if (itemIndex < items.size) {
                    map[Items.LEVEL_PADERBORN] = index
                    itemIndex++
                }
            }
        }

        return GUIBuilder("Level Select", rows * cols)
            .addItems(map)
            .fillerPanel()
            .build()
    }
}