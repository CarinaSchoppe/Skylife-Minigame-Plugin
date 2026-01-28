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
     * Handles purchase flow, selection, and deselection based on kit state.
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

        // Skip glass panes and info items
        if (clickedItem.type == Material.GRAY_STAINED_GLASS_PANE || clickedItem.type == Material.PAPER) return

        // Handle locked kits (BARRIER)
        if (clickedItem.type == Material.BARRIER) {
            // Find kit by searching lore for price
            val kit = findKitFromItem(clickedItem)
            if (kit != null) {
                // Open purchase confirmation GUI
                com.carinaschoppe.skylife.utility.ui.KitPurchaseConfirmGui.open(player, kit)
            }
            return
        }

        // Find matching kit by comparing base display names (strip selection indicator)
        val clickedDisplayName = clickedItem.itemMeta?.displayName()
        if (clickedDisplayName == null) return

        val kit = KitManager.kits.find { kit ->
            val kitIconName = kit.icon.toItemStack().itemMeta?.displayName()
            val strippedClickedName = plainText.serialize(clickedDisplayName).replace("✓ ", "")
            val strippedKitName = plainText.serialize(kitIconName ?: net.kyori.adventure.text.Component.empty())
            strippedClickedName.equals(strippedKitName, ignoreCase = true)
        }

        if (kit != null) {
            val isUnlocked = com.carinaschoppe.skylife.economy.KitUnlockManager.hasUnlocked(player.uniqueId, kit)
            val isSelected = KitManager.getSelectedKits(player).contains(kit)

            if (!isUnlocked && kit.rarity.price > 0) {
                // Open purchase GUI
                com.carinaschoppe.skylife.utility.ui.KitPurchaseConfirmGui.open(player, kit)
            } else if (isSelected) {
                // Deselect kit
                KitManager.deselectKit(player, kit)
                player.sendMessage(Messages.KIT_DESELECTED(kit.name))

                // Update scoreboard
                val game = GameCluster.getGamePlayerIsIn(player)
                if (game != null) {
                    ScoreboardManager.updateScoreboard(player, game)
                }

                // Reopen GUI to refresh
                KitSelectorGui.open(player)
            } else {
                // Select kit
                val success = KitManager.selectKit(player, kit)
                if (success) {
                    player.sendMessage(Messages.KIT_SELECTED(kit.name))

                    // Update scoreboard
                    val game = GameCluster.getGamePlayerIsIn(player)
                    if (game != null) {
                        ScoreboardManager.updateScoreboard(player, game)
                    }

                    // Reopen GUI to refresh
                    KitSelectorGui.open(player)
                } else {
                    player.sendMessage(Messages.KIT_SELECTION_FAILED_SLOTS_FULL)
                }
            }
        }
    }

    /**
     * Finds a kit by searching through available kits.
     * Used for locked kits displayed as barriers.
     */
    private fun findKitFromItem(item: org.bukkit.inventory.ItemStack): com.carinaschoppe.skylife.game.kit.Kit? {
        val lore = item.itemMeta?.lore() ?: return null
        val plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()

        // Look for price in lore
        for (line in lore) {
            val lineText = plainText.serialize(line)
            if (lineText.contains("Price:", ignoreCase = true)) {
                val priceStr = lineText.replace("Price:", "").replace("Coins", "").trim()
                val price = priceStr.toIntOrNull() ?: continue

                // Find kit with matching price
                return KitManager.kits.find { it.rarity.price == price }
            }
        }

        return null
    }
}

