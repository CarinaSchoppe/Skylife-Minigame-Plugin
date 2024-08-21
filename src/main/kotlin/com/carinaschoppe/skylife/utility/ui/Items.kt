package com.carinaschoppe.skylife.utility.ui

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object Items {


    val FILLER_PANEL: ItemStack = ItemBuilder(Material.GLASS_PANE).addName("").addEnchantment(Enchantment.LOYALTY, 1).build()

    val LEVEL_PADERBORN: ItemStack = ItemBuilder(Material.IRON_SHOVEL).addName("Paderborn").addLore("Minilevel fast Rounds").build()
}