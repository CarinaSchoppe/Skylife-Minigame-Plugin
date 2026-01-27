package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.ui.GUIs
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems.NavAction
import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolderFactory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * Listens for player interactions with the game selection GUI.
 *
 * This listener handles clicks in the game overview inventory, allowing players
 * to select and join games by clicking on items that represent available games.
 * It's typically used in conjunction with a GUI that displays all available games.
 */
class PlayerSelectGameListener : Listener {

    /**
     * Handles inventory click events in the game selection GUI.
     *
     * This method is triggered when a player clicks on an item in any inventory.
     * It checks if the click occurred in a GameOverview inventory and, if so,
     * processes the click to join the selected game.
     *
     * @param event The InventoryClickEvent that was triggered
     */
    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        // Only process clicks when the game overview is open
        val holder = event.view.topInventory.holder
        if (holder !is GameOverviewHolderFactory) {
            return
        }

        // Cancel the event to prevent item pickup/placement
        event.isCancelled = true

        // Only react to clicks in the top inventory
        if (event.clickedInventory != event.view.topInventory) {
            return
        }

        val item = event.currentItem ?: return

        val navAction = GameOverviewItems.getNavAction(item)
        if (navAction != null) {
            val nextPage = when (navAction) {
                NavAction.NEXT -> holder.page + 1
                NavAction.PREVIOUS -> holder.page - 1
            }
            if (nextPage in 0 until holder.totalPages) {
                (event.whoClicked as? Player)?.openInventory(GUIs.levelSelectInventory(nextPage))
            }
            return
        }

        val gameName = GameOverviewItems.getGameName(item) ?: return
        val player = event.whoClicked as? Player ?: return

        if (GameCluster.addPlayerToGame(player, gameName)) {
            player.closeInventory()
        } else {
            player.sendMessage(com.carinaschoppe.skylife.utility.messages.Messages.ERROR_GAME_FULL_OR_STARTED())
        }
    }
}
