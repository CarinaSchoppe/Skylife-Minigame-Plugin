package com.carinaschoppe.skylife.utility.ui.inventoryholders

import com.carinaschoppe.skylife.platform.PluginContext
import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

/**
 * An abstract base class for creating custom inventory holders with a specified name and size.
 *
 * This class implements the Bukkit [InventoryHolder] interface and provides a foundation
 * for creating custom inventory-based GUIs. It handles the creation and management of
 * the underlying Bukkit inventory.
 *
 * @property inventoryName The display name of the inventory, shown at the top of the GUI
 * @property size The size of the inventory in slots. Must be a multiple of 9 and between 9-54
 */
abstract class InventoryHolderFactory(val inventoryName: String, val size: Int) : InventoryHolder {

    /**
     * The internal Bukkit inventory instance.
     *
     * This is initialized when [initInventory()] is called and holds all the items
     * displayed in this inventory.
     */
    lateinit var internalInventory: Inventory

    /**
     * Initializes the internal inventory with the specified name and size.
     *
     * This method must be called before the inventory can be used. It creates a new
     * inventory with the configured name and size, and returns the factory instance
     * for method chaining.
     *
     * @return This [InventoryHolderFactory] instance for method chaining
     */
    open fun initInventory(): InventoryHolderFactory {
        internalInventory = PluginContext.plugin.server.createInventory(this, size, Component.text(inventoryName))
        return this
    }

    /**
     * Gets the Bukkit inventory associated with this holder.
     *
     * This is an implementation of the [InventoryHolder] interface method.
     *
     * @return The [Inventory] instance managed by this holder
     * @throws UninitializedPropertyAccessException if [initInventory()] hasn't been called yet
     */
    override fun getInventory(): Inventory {
        return this.internalInventory
    }
}
