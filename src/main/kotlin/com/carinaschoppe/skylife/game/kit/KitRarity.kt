package com.carinaschoppe.skylife.game.kit

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Rarity levels for kits with associated pricing and display colors.
 *
 * @property displayName The friendly name of the rarity
 * @property color The display color for this rarity in GUIs
 * @property price The cost in coins to unlock a kit of this rarity
 */
enum class KitRarity(
    val displayName: String,
    val color: NamedTextColor,
    val price: Int
) {
    /** Common rarity - free kits, always unlocked */
    COMMON("Common", NamedTextColor.WHITE, 0),

    /** Rare rarity - costs 500 coins */
    RARE("Rare", NamedTextColor.BLUE, 500),

    /** Epic rarity - costs 1500 coins */
    EPIC("Epic", NamedTextColor.DARK_PURPLE, 1500),

    /** Legendary rarity - costs 5000 coins */
    LEGENDARY("Legendary", NamedTextColor.GOLD, 5000);

    /**
     * Gets the colored and bolded display name as a Component.
     *
     * @return Component with the rarity name in the appropriate color and bold formatting
     */
    fun getColoredName(): net.kyori.adventure.text.Component {
        return net.kyori.adventure.text.Component.text(displayName, color, net.kyori.adventure.text.format.TextDecoration.BOLD)
    }
}
