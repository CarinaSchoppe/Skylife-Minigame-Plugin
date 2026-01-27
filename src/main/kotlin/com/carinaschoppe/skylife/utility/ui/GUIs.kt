package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems.NavAction
import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolderFactory
import com.carinaschoppe.skylife.utility.ui.inventoryholders.SkillSelectorHolderFactory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * A collection of predefined GUI inventories used throughout the plugin.
 *
 * This object provides factory methods for creating different types of GUI inventories
 * with preconfigured layouts and items. Each method returns a new [Inventory] instance
 * that can be displayed to players.
 *
 * @see GUIBuilder
 * @see Items
 */
object GUIs {

    /**
     * Creates an inventory for skill selection.
     *
     * This inventory displays available skills that players can select.
     * Currently, it only shows the ANTI_FALL_BOOTS item as a placeholder.
     *
     * @return A new [Inventory] instance for skill selection
     * @see Items.ANTI_FALL_BOOTS
     */
    val SKILL_SELECT_INVENTORY = fun(): Inventory {
        val itemMap = mutableMapOf<ItemStack, Int>()

        //add items here

        return GUIBuilder(SkillSelectorHolderFactory().initInventory()).addItems(itemMap).fillerPanel().build()
    }

    /**
     * Creates an inventory for level selection.
     *
     * This inventory displays available levels/maps that players can choose from.
     * Items are placed in a grid layout, skipping the outer border slots.
     * Currently, it only shows the LEVEL_PADERBORN item as a placeholder.
     *
     * @return A new [Inventory] instance for level selection
     * @see Items.LEVEL_PADERBORN
     */
    fun levelSelectInventory(page: Int = 0): Inventory {
        val games = GameCluster.lobbyGamesList

        val inventorySize = GameOverviewHolderFactory.INVENTORY_SIZE
        val navigationSlots = setOf(45, 53)
        val gameSlotsCount = inventorySize - 2

        val totalPages = if (games.isEmpty()) 1 else ((games.size - 1) / gameSlotsCount) + 1
        val safePage = page.coerceIn(0, totalPages - 1)

        val holder = GameOverviewHolderFactory(safePage, totalPages).initInventory()
        val builder = GUIBuilder(holder)

        val pageGames = games.drop(safePage * gameSlotsCount).take(gameSlotsCount)

        // Fill games from top-left to bottom-right, skipping navigation slots
        var gameIdx = 0
        for (slot in 0 until inventorySize) {
            if (slot in navigationSlots) continue

            val game = pageGames.getOrNull(gameIdx)
            if (game != null) {
                builder.setItem(slot, GameOverviewItems.createGameItem(game))
                gameIdx++
            }
        }

        if (safePage > 0) {
            builder.setItem(45, GameOverviewItems.createNavItem(NavAction.PREVIOUS))
        }
        if (safePage < totalPages - 1) {
            builder.setItem(53, GameOverviewItems.createNavItem(NavAction.NEXT))
        }

        return builder.fillerPanel().build()
    }
}
