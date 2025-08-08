package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.utility.ui.inventoryholders.InventoryHolderFactory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * A builder class for creating and customizing Minecraft GUI inventories.
 *
 * This class provides a fluent interface for building and customizing inventory UIs.
 * It works in conjunction with [InventoryHolderFactory] to create consistent and
 * well-structured GUIs with features like filler panels and item placement.
 *
 * @property holder The [InventoryHolderFactory] that manages the underlying inventory
 */
class GUIBuilder(private val holder: InventoryHolderFactory) {

    /**
     * Finalizes and returns the built inventory.
     *
     * @return The constructed [Inventory] ready to be displayed to players
     */
    fun build(): Inventory = holder.inventory

    /**
     * Sets an item at the specified slot in the inventory.
     *
     * @param slot The slot number where the item should be placed
     * @param item The [ItemStack] to place in the slot
     * @return This [GUIBuilder] instance for method chaining
     */
    fun setItem(slot: Int, item: ItemStack): GUIBuilder = apply {
        holder.inventory.setItem(slot, item)
    }

    /**
     * Alternative syntax for [setItem] with parameters in reverse order.
     *
     * @param item The [ItemStack] to place in the slot
     * @param slot The slot number where the item should be placed
     * @return This [GUIBuilder] instance for method chaining
     */
    fun setItem(item: ItemStack, slot: Int): GUIBuilder = setItem(slot, item)

    /**
     * Fills all empty slots in the inventory with the filler panel item.
     *
     * This is typically used to create a consistent background for the GUI.
     * The filler panel is defined in the [Items] object.
     *
     * @return This [GUIBuilder] instance for method chaining
     * @see Items.FILLER_PANEL
     */
    fun fillerPanel(): GUIBuilder = apply {
        for (i in 0 until holder.internalInventory.size) {
            if (holder.inventory.getItem(i) == null) {
                setItem(i, Items.FILLER_PANEL)
            }
        }
    }

    /**
     * Adds multiple items to the inventory at their specified slots.
     *
     * @param items A map of [ItemStack] to slot numbers where each item should be placed
     * @return This [GUIBuilder] instance for method chaining
     */
    fun addItems(items: Map<ItemStack, Int>): GUIBuilder = apply {
        items.forEach { (item, slot) -> setItem(slot, item) }
    }

}