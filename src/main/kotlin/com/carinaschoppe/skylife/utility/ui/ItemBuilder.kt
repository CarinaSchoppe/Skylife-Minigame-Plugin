package com.carinaschoppe.skylife.utility.ui

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder(itemMaterial: Material) {

    private var item: ItemStack = ItemStack(itemMaterial)

    private var itemMeta: ItemMeta = item.itemMeta

    fun addName(name: Component): ItemBuilder {
        itemMeta.displayName(name)
        return this
    }

    fun addName(name: String): ItemBuilder {
        itemMeta.displayName(Component.text(name))
        return this
    }

    fun addEnchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        item.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun addAmount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
        return this
    }

    fun build(): ItemStack {
        item.setItemMeta(itemMeta)
        return item
    }

    fun addLore(vararg lore: Component): ItemBuilder {
        itemMeta.lore(lore.toList())
        return this
    }

    fun addLore(vararg lore: String): ItemBuilder {
        itemMeta.lore(lore.map { Component.text(it) }.toList())
        return this
    }
}