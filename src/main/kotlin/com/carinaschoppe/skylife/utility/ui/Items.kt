package com.carinaschoppe.skylife.utility.ui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * A collection of predefined [ItemStack]s used throughout the plugin.
 *
 * This object contains static references to commonly used items in the plugin,
 * such as UI elements, skill items, and other game-related items.
 * All items are created using the [ItemBuilder] for consistency.
 */
object Items {

    /**
     * A transparent panel item used as a filler in GUI menus.
     *
     * This item has an empty name and a hidden enchantment effect to make it
     * visually distinct while maintaining a clean appearance.
     */
    val FILLER_PANEL: ItemStack = ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
        .addName(" ")
        .build()


}