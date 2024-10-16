package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolderFactory
import com.carinaschoppe.skylife.utility.ui.inventoryholders.SkillSelectorHolderFactory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object GUIs {


    //TODO: Implement functionality with the items and listeners of events
    val SKILL_SELECT_INVENTORY = fun(): Inventory {
        val itemMap = mutableMapOf<ItemStack, Int>()

        //add items here
        itemMap[Items.ANTI_FALL_BOOTS] = 1


        return GUIBuilder(SkillSelectorHolderFactory().initInventory()).addItems(itemMap).fillerPanel().build()
    }

    val LEVEL_SELECT_INVENTORY = fun(): Inventory {

        val itemMap = mutableMapOf<ItemStack, Int>()

        val rows = 9
        val cols = 4


        // Sample list of items to place into the grid
        val itemsToAdd = listOf(Items.LEVEL_PADERBORN)

        var itemIndex = 0

// Iterate over the entire grid, but place items only in the inner part
        for (index in 0 until (rows * cols)) {
            val row = index / cols
            val col = index % cols

            // Skip the first and last rows and columns
            if (row in 1 until (rows - 1) && col in 1 until (cols - 1) && itemIndex < itemsToAdd.size) {
                itemMap[itemsToAdd[itemIndex]] = index
                itemIndex++
            }
        }

        return GUIBuilder(GameOverviewHolderFactory().initInventory())
            .addItems(itemMap)
            .fillerPanel()
            .build()
    }
}