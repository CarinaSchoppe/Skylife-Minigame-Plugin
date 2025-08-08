package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameOverviewHolderFactory
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
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
        // Only process clicks in the GameOverview inventory
        if (event.clickedInventory?.getHolder(false) !is GameOverviewHolderFactory) {
            return
        }

        // Cancel the event to prevent item pickup/placement
        event.isCancelled = true

        // Get the clicked item and its display name
        val item = event.currentItem ?: return
        val name = PlainTextComponentSerializer.plainText().serialize(item.itemMeta?.displayName() ?: return)

        // Ignore clicks on empty-named items
        if (name.isEmpty()) {
            return
        }

        // Ensure the clicker is a player
        if (event.whoClicked !is Player) {
            return
        }

        // Execute the join command for the selected game
        (event.whoClicked as Player).performCommand("join $name")
    }
}