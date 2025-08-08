package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolderFactory
import com.carinaschoppe.skylife.utility.ui.inventoryholders.SkillSelectorHolderFactory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * A collection of predefined GUI inventories used throughout the plugin.
 *
 * This object provides factory methods for creating different types of GUI inventories
 * with preconfigured layouts and items. Each method returns a new [Inventory] instance
 * that can be displayed to players.
 *
 * @see GUIBuilder
 * @see Items
 */
object GUIs {

    /**
     * Creates an inventory for skill selection.
     *
     * This inventory displays available skills that players can select.
     * Currently, it only shows the ANTI_FALL_BOOTS item as a placeholder.
     *
     * @return A new [Inventory] instance for skill selection
     */
    val SKILL_SELECT_INVENTORY = fun(): Inventory {
        val itemMap = mutableMapOf<ItemStack, Int>()

        //add items here

        return GUIBuilder(SkillSelectorHolderFactory().initInventory()).addItems(itemMap).fillerPanel().build()
    }

    /**
     * Creates an inventory for level selection.
     *
     * This inventory displays available levels/maps that players can choose from.
     * Items are placed in a grid layout, skipping the outer border slots.
     * Currently, it only shows the LEVEL_PADERBORN item as a placeholder.
     *
     * @return A new [Inventory] instance for level selection
     * @see Items.LEVEL_PADERBORN
     */
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

            // Skip the first and last rows and columns to create a border
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