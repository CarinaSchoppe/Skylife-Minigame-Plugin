package com.carinaschoppe.skylife.game.kit

import org.bukkit.Material

/**
 * A builder class for creating [Kit] instances using a fluent interface.
 *
 * @param name The name of the kit to be built.
 */
class KitBuilder(private val name: String) {
    private var icon: KitItem = KitItem(Material.STONE, name = name)
    private val items = mutableListOf<KitItem>()
    private var rarity: KitRarity = KitRarity.COMMON

    /**
     * Sets the icon for the kit.
     *
     * @param kitItem The [KitItem] to be used as the icon.
     * @return This [KitBuilder] instance for chaining.
     */
    fun icon(kitItem: KitItem): KitBuilder {
        this.icon = kitItem
        return this
    }

    /**
     * Adds an item to the kit.
     *
     * @param kitItem The [KitItem] to add to the kit's contents.
     * @return This [KitBuilder] instance for chaining.
     */
    fun item(kitItem: KitItem): KitBuilder {
        items.add(kitItem)
        return this
    }

    /**
     * Sets the rarity for the kit.
     *
     * @param rarity The [KitRarity] level.
     * @return This [KitBuilder] instance for chaining.
     */
    fun rarity(rarity: KitRarity): KitBuilder {
        this.rarity = rarity
        return this
    }

    /**
     * Constructs the final [Kit] instance.
     *
     * @return The fully configured [Kit].
     */
    fun build(): Kit {
        return Kit(name, icon, items, rarity)
    }
}
