package com.carinaschoppe.skylife.game.kit

/**
 * Represents a complete kit that a player can select.
 *
 * @property name The unique name of the kit.
 * @property icon The [KitItem] used as an icon in the selection GUI.
 * @property items A list of [KitItem]s that the player receives upon starting a game with this kit.
 * @property rarity The rarity level of this kit (determines price and color).
 */
class Kit(
    val name: String,
    val icon: KitItem,
    val items: List<KitItem>,
    val rarity: KitRarity = KitRarity.COMMON
)
