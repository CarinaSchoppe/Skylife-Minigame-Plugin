package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

/**
 * Listener to prevent skill activation items from being manipulated.
 * Prevents Pilot Wings (Elytra) and Ninja Cloak (Gray Dye) from being:
 * - Dropped
 * - Equipped as armor
 * - Moved in inventory (to armor slots)
 * - Swapped to offhand
 */
class SkillItemProtectionListener : Listener {

    /**
     * Checks if an item is a protected skill item.
     */
    private fun isProtectedSkillItem(item: ItemStack?): Boolean {
        if (item == null) return false

        val itemName = item.itemMeta?.displayName()?.toString() ?: return false

        return itemName.contains("Pilot Wings") || itemName.contains("Ninja Cloak")
    }

    /**
     * Prevents dropping of skill items.
     */
    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        val game = GameCluster.getGame(player) ?: return

        // Only protect items during active games
        if (game.spectators.contains(player)) return

        val item = event.itemDrop.itemStack
        if (isProtectedSkillItem(item)) {
            event.isCancelled = true
            player.sendMessage("§cYou cannot drop this skill item!")
        }
    }

    /**
     * Prevents moving skill items to armor slots or other restricted actions.
     */
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? org.bukkit.entity.Player ?: return
        val game = GameCluster.getGame(player) ?: return

        // Only protect items during active games
        if (game.spectators.contains(player)) return

        val clickedItem = event.currentItem
        val cursorItem = event.cursor

        // Prevent moving Pilot Wings to armor slot (helmet slot)
        if (clickedItem != null && clickedItem.type == Material.ELYTRA) {
            val itemName = clickedItem.itemMeta?.displayName()?.toString() ?: ""
            if (itemName.contains("Pilot Wings")) {
                // Check if trying to move to armor slots (39 = helmet, 38 = chestplate)
                if (event.slot == 39 || event.slot == 38) {
                    event.isCancelled = true
                    player.sendMessage("§cYou cannot equip this skill item!")
                    return
                }
            }
        }

        // Prevent shift-clicking skill items to armor slots
        if (event.isShiftClick) {
            if (isProtectedSkillItem(clickedItem)) {
                // Allow shift-clicking within player inventory, but not to armor slots
                val clickedSlot = event.slot
                // Armor slots are typically 36-39 (boots, leggings, chestplate, helmet)
                if (clickedSlot in 36..39 || event.slotType == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR) {
                    event.isCancelled = true
                    player.sendMessage("§cYou cannot equip this skill item!")
                }
            }
        }

        // Prevent moving skill items with cursor
        if (isProtectedSkillItem(cursorItem)) {
            val targetSlot = event.slot
            // Prevent placing in armor slots
            if (targetSlot in 36..39 || event.slotType == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR) {
                event.isCancelled = true
                player.sendMessage("§cYou cannot equip this skill item!")
            }
        }
    }

    /**
     * Prevents swapping skill items to offhand (F key).
     */
    @EventHandler
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val game = GameCluster.getGame(player) ?: return

        // Only protect items during active games
        if (game.spectators.contains(player)) return

        val mainHandItem = event.mainHandItem
        val offHandItem = event.offHandItem

        if (isProtectedSkillItem(mainHandItem) || isProtectedSkillItem(offHandItem)) {
            event.isCancelled = true
            player.sendMessage("§cYou cannot swap this skill item to your offhand!")
        }
    }
}
