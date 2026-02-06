package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import org.bukkit.Material
import org.bukkit.entity.Player
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
    private companion object {
        const val PILOT_WINGS_NAME = "Pilot Wings"
    }

    /**
     * Checks if an item is a protected skill item.
     */
    private fun isProtectedSkillItem(item: ItemStack?): Boolean {
        if (item == null) return false

        val itemName = item.itemMeta?.displayName()?.toString() ?: return false

        return itemName.contains(PILOT_WINGS_NAME) || itemName.contains("Ninja Cloak")
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
        val player = event.whoClicked as? Player ?: return
        val game = GameCluster.getGame(player) ?: return

        // Only protect items during active games
        if (game.spectators.contains(player)) return

        val clickedItem = event.currentItem
        val cursorItem = event.cursor

        if (isPilotWingsEquipBlocked(clickedItem, event)) {
            cancelEquip(player, event)
            return
        }

        if (event.isShiftClick && isProtectedSkillItem(clickedItem) && isArmorSlot(event)) {
            cancelEquip(player, event)
            return
        }

        if (isProtectedSkillItem(cursorItem) && isArmorSlot(event)) {
            cancelEquip(player, event)
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

    private fun isPilotWingsEquipBlocked(item: ItemStack?, event: InventoryClickEvent): Boolean {
        if (item == null || item.type != Material.ELYTRA) {
            return false
        }
        val itemName = item.itemMeta?.displayName()?.toString() ?: ""
        if (!itemName.contains(PILOT_WINGS_NAME)) {
            return false
        }
        return event.slot == 39 || event.slot == 38
    }

    private fun isArmorSlot(event: InventoryClickEvent): Boolean {
        return event.slot in 36..39 || event.slotType == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR
    }

    private fun cancelEquip(player: Player, event: InventoryClickEvent) {
        event.isCancelled = true
        player.sendMessage("§cYou cannot equip this skill item!")
    }
}
