package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.Skylife
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Rarity levels for skills with associated pricing and display colors.
 * Prices are loaded from config.json and can be customized.
 *
 * @property displayName The friendly name of the rarity
 * @property color The display color for this rarity in GUIs
 */
enum class SkillRarity(
    val displayName: String,
    val color: NamedTextColor
) {
    /** Common rarity - free skills, always unlocked (price from config) */
    COMMON("Common", NamedTextColor.WHITE),

    /** Rare rarity - costs coins (price from config, default 500) */
    RARE("Rare", NamedTextColor.BLUE),

    /** Epic rarity - costs coins (price from config, default 1500) */
    EPIC("Epic", NamedTextColor.DARK_PURPLE),

    /** Legendary rarity - costs coins (price from config, default 5000) */
    LEGENDARY("Legendary", NamedTextColor.GOLD);

    /**
     * Gets the price for this rarity from the configuration.
     *
     * @return The cost in coins to unlock a skill of this rarity
     */
    val price: Int
        get() = when (this) {
            COMMON -> Skylife.config.skillPrices.common
            RARE -> Skylife.config.skillPrices.rare
            EPIC -> Skylife.config.skillPrices.epic
            LEGENDARY -> Skylife.config.skillPrices.legendary
        }

    /**
     * Gets the colored and bolded display name as a Component.
     *
     * @return Component with the rarity name in the appropriate color and bold formatting
     */
    fun getColoredName(): net.kyori.adventure.text.Component {
        return net.kyori.adventure.text.Component.text(displayName, color, net.kyori.adventure.text.format.TextDecoration.BOLD)
    }
}
