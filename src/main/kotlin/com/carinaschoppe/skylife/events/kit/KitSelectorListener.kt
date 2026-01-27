package com.carinaschoppe.skylife.events.kit

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.game.kit.KitSelectorGui
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.scoreboard.ScoreboardManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Handles all interactions related to the kit selection process.
 * This includes opening the GUI and processing the player's choice.
 */
class KitSelectorListener : Listener {

    companion object {
        /** The display name for the item that opens the kit selector GUI. */
        val KIT_SELECTOR_ITEM_NAME = "<green><bold>Kit Selector</bold></green>"
    }

    /**
     * Opens the kit selector GUI when a player right-clicks the selector item (a chest).
     *
     * @param event The event triggered by a player interaction.
     */
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        val item = event.item ?: return
        if (item.type == Material.CHEST && item.itemMeta?.displayName() == Messages.parse(KIT_SELECTOR_ITEM_NAME)) {
            KitSelectorGui.open(event.player)
            event.isCancelled = true
        }
    }

    /**
     * Handles a player's click inside the kit selector GUI.
     * If a valid kit icon is clicked, it selects the kit for the player, sends a confirmation message,
     * updates the scoreboard, and closes the inventory.
     *
     * @param event The event triggered by a click in an inventory.
     */
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title() != Messages.parse(KitSelectorGui.GUI_TITLE)) return

        event.isCancelled = true

        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as? Player ?: return

        val kit = KitManager.kits.find { kit ->
            clickedItem.itemMeta?.displayName() == kit.icon.toItemStack().itemMeta.displayName()
        }

        if (kit != null) {
            KitManager.selectKit(player, kit)
            player.sendMessage(Messages.KIT_SELECTED(kit.name))

            // Update scoreboard
            val game = GameCluster.getGamePlayerIsIn(player)
            if (game != null) {
                ScoreboardManager.updateScoreboard(player, game)
            }

            player.closeInventory()
        }
    }
}

