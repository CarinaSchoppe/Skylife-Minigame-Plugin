package com.carinaschoppe.skylife.game.items

import org.bukkit.inventory.ItemStack

abstract class Kit(val name: String) {

    val items = mutableSetOf<ItemStack>()

}