package com.carinaschoppe.skylife.utility.ui

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
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
    val FILLER_PANEL: ItemStack = ItemBuilder(Material.GLASS_PANE)
        .addName("")
        .addEnchantment(Enchantment.LOYALTY, 1)  // Creates the visual enchantment effect
        .build()

    /**
     * A placeholder item representing the "Paderborn" level/map.
     *
     * Currently used as a test item in level selection menus.
     * TODO: Replace with actual level representation with proper name and description
     */
    val LEVEL_PADERBORN: ItemStack = ItemBuilder(Material.IRON_SHOVEL)
        .addName("Paderborn")
        .addLore("Minigame: Fast Rounds")
        .build()
}