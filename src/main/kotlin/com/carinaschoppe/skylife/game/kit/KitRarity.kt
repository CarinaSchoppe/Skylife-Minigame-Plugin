package com.carinaschoppe.skylife.game.kit

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Rarity levels for kits with associated pricing and colors.
 */
enum class KitRarity(
    val displayName: String,
    val color: NamedTextColor,
    val price: Int
) {
    COMMON("Common", NamedTextColor.WHITE, 0),
    RARE("Rare", NamedTextColor.BLUE, 500),
    EPIC("Epic", NamedTextColor.DARK_PURPLE, 1500),
    LEGENDARY("Legendary", NamedTextColor.GOLD, 5000);

    /**
     * Gets the colored display name.
     */
    fun getColoredName(): net.kyori.adventure.text.Component {
        return net.kyori.adventure.text.Component.text(displayName, color, net.kyori.adventure.text.format.TextDecoration.BOLD)
    }
}
