package com.carinaschoppe.skylife.utility.ui.inventoryholders

/**
 * A specialized [InventoryHolderFactory] for creating game overview GUIs.
 *
 * This factory creates a 6-row (54-slot) inventory with a title of "Game Overview".
 * It's designed to be used as a base for displaying game-related information
 * and controls to players, such as available games, game status, or other
 * game management options.
 *
 * The actual content of the inventory should be populated by the code that
 * uses this factory, typically by adding items to the inventory after creation.
 *
 * @property page The current page index (0-based).
 * @property totalPages The total number of available pages.
 * @see InventoryHolderFactory
 */
class GameOverviewHolderFactory(val page: Int, val totalPages: Int) :
    InventoryHolderFactory(titleFor(page, totalPages), INVENTORY_SIZE) {

    override fun initInventory(): GameOverviewHolderFactory {
        super.initInventory()
        return this
    }

    companion object {
        const val INVENTORY_SIZE = 54

        fun titleFor(page: Int, totalPages: Int): String {
            return if (totalPages <= 1) {
                "Spieleübersicht"
            } else {
                "Spieleübersicht (${page + 1}/$totalPages)"
            }
        }
    }
}
