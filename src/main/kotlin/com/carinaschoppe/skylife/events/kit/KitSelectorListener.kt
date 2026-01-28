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
        // Only proceed if kits are enabled
        if (!KitManager.areKitsEnabled()) return

        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        val item = event.item ?: return
        if (item.type != Material.CHEST) return

        val meta = item.itemMeta ?: return
        val displayName = meta.displayName() ?: return

        // Use PlainTextComponentSerializer for reliable comparison
        val plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(displayName)

        if (plainText.contains("Kit Selector", ignoreCase = true)) {
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
        // Only proceed if kits are enabled
        if (!KitManager.areKitsEnabled()) return

        // Check if this is the kit selector GUI
        val plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
        val titleText = plainText.serialize(event.view.title())

        if (!titleText.contains("Select your Kit", ignoreCase = true)) return

        event.isCancelled = true

        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as? Player ?: return

        // Skip glass panes
        if (clickedItem.type == Material.GRAY_STAINED_GLASS_PANE) return

        // Find matching kit by comparing display names
        val clickedDisplayName = clickedItem.itemMeta?.displayName()
        if (clickedDisplayName == null) return

        val kit = KitManager.kits.find { kit ->
            val kitIconName = kit.icon.toItemStack().itemMeta?.displayName()
            kitIconName == clickedDisplayName
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

