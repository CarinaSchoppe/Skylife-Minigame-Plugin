package com.carinaschoppe.skylife.game.kit

import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * Represents a single item within a kit, including its material, amount, name, lore, and enchantments.
 *
 * @property material The [Material] of the item.
 * @property amount The quantity of the item.
 * @property name The custom display name of the item. Supports legacy color codes.
 * @property lore A list of [Component]s for the item's lore.
 * @property enchantments A map of [Enchantment] to level for the item's enchantments.
 */
class KitItem(
    val material: Material,
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<Component> = emptyList(),
    val enchantments: Map<Enchantment, Int> = emptyMap()
) {
    /**
     * Converts this KitItem data into a tangible [ItemStack].
     *
     * @return The resulting Bukkit ItemStack.
     */
    fun toItemStack(): ItemStack {
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta

        name?.let { itemMeta.displayName(Messages.legacy(it)) }
        if (lore.isNotEmpty()) {
            //TODO: here deprecated
            itemMeta.loreComponents(lore)
        }
        if (enchantments.isNotEmpty()) {
            enchantments.forEach { (enchant, level) ->
                itemMeta.addEnchant(enchant, level, true)
            }
        }

        itemStack.itemMeta = itemMeta
        return itemStack
    }
}