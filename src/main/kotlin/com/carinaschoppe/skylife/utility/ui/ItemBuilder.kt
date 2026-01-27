package com.carinaschoppe.skylife.utility.ui

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * A fluent builder class for creating and customizing Minecraft [ItemStack]s.
 *
 * This class provides a chainable interface for creating items with various properties
 * such as display name, lore, enchantments, and stack size. It handles the underlying
 * [ItemMeta] operations internally for a cleaner API.
 *
 * @property itemMaterial The base material for the item being built
 * @constructor Creates a new ItemBuilder for the specified material
 */
class ItemBuilder(private val itemMaterial: Material) {

    /** The underlying ItemStack being built */
    private var item: ItemStack = ItemStack(itemMaterial)

    /** The ItemMeta for the current item */
    private var itemMeta: ItemMeta = item.itemMeta!!

    /**
     * Sets the display name of the item using a Component.
     *
     * @param name The display name as a Component
     * @return This [ItemBuilder] instance for method chaining
     */
    fun addName(name: Component): ItemBuilder = apply {
        itemMeta.displayName(name)
    }

    /**
     * Sets the display name of the item using a plain string.
     * The string will be converted to a text Component.
     *
     * @param name The display name as a plain string
     * @return This [ItemBuilder] instance for method chaining
     */
    fun addName(name: String): ItemBuilder = apply {
        itemMeta.displayName(Component.text(name))
    }

    /**
     * Adds an enchantment to the item, bypassing level restrictions.
     *
     * @param enchantment The [Enchantment] to add
     * @param level The level of the enchantment
     * @return This [ItemBuilder] instance for method chaining
     * @see ItemStack.addUnsafeEnchantment
     */
    fun addEnchantment(enchantment: Enchantment, level: Int): ItemBuilder = apply {
        item.addUnsafeEnchantment(enchantment, level)
    }

    /**
     * Sets the stack size of the item.
     *
     * @param amount The number of items in the stack (1-64)
     * @return This [ItemBuilder] instance for method chaining
     * @throws IllegalArgumentException if amount is not between 1 and 64 (inclusive)
     */
    fun addAmount(amount: Int): ItemBuilder = apply {
        require(amount in 1..64) { "Item amount must be between 1 and 64" }
        item.amount = amount
    }

    /**
     * Finalizes and returns the built [ItemStack].
     *
     * This applies all the configured properties and returns the final item.
     * No further modifications should be made after calling this method.
     *
     * @return The constructed [ItemStack]
     */
    fun build(): ItemStack {
        item.itemMeta = itemMeta
        return item
    }

    fun modifyMeta(action: (ItemMeta) -> Unit): ItemBuilder = apply {
        action(itemMeta)
    }

    /**
     * Sets the item's lore using an array of Components.
     * This will replace any existing lore.
     *
     * @param lore Vararg of Components to use as lore
     * @return This [ItemBuilder] instance for method chaining
     */
    fun addLore(vararg lore: Component): ItemBuilder = apply {
        itemMeta.lore(lore.toList())
    }

    /**
     * Sets the item's lore using an array of strings.
     * Each string will be converted to a text Component.
     * This will replace any existing lore.
     *
     * @param lore Vararg of strings to use as lore
     * @return This [ItemBuilder] instance for method chaining
     */
    fun addLore(vararg lore: String): ItemBuilder = apply {
        itemMeta.lore(lore.map { Component.text(it) }.toList())
    }
}