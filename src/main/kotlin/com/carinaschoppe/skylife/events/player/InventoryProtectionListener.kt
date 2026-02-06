package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.ui.ExitDoorItem
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Protects special items (compass, star, exit door) from being dropped, moved, or modified.
 * These items should only be interactable to open their respective functions.
 */
class InventoryProtectionListener : Listener {

    /**
     * Prevents dropping of special items (compass, star, exit door).
     */
    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack

        if (GameOverviewItems.isMenuItem(item) ||
            isSkillsItem(item) ||
            ExitDoorItem.isExitDoor(item)
        ) {
            event.isCancelled = true
        }
    }

    /**
     * Prevents moving special items in inventory.
     */
    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val currentItem = event.currentItem
        val cursor = event.cursor

        if (GameOverviewItems.isMenuItem(currentItem) ||
            GameOverviewItems.isMenuItem(cursor) ||
            isSkillsItem(currentItem) ||
            isSkillsItem(cursor) ||
            ExitDoorItem.isExitDoor(currentItem) ||
            ExitDoorItem.isExitDoor(cursor)
        ) {
            event.isCancelled = true
        }
    }

    /**
     * Prevents dragging special items.
     */
    @EventHandler(ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        val item = event.oldCursor

        if (GameOverviewItems.isMenuItem(item) ||
            isSkillsItem(item) ||
            ExitDoorItem.isExitDoor(item)
        ) {
            event.isCancelled = true
        }
    }

    /**
     * Handles clicking the exit door to teleport to hub.
     * Only works in Lobby, End, and Hub states - not during active gameplay.
     */
    @EventHandler(ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (!ExitDoorItem.isExitDoor(item)) {
            return
        }

        event.isCancelled = true
        val player = event.player

        // Check if player is in a game
        val game = GameCluster.getGamePlayerIsIn(player)

        // Only allow exit door in Lobby/End states, or when not in a game (Hub)
        if (game != null && game.currentState !is com.carinaschoppe.skylife.game.gamestates.LobbyState
            && game.currentState !is com.carinaschoppe.skylife.game.gamestates.EndState
        ) {
            // Player is in InGame state - don't allow leaving
            return
        }

        // Remove player from game if they're in one
        GameCluster.removePlayerFromGame(player)
    }

    /**
     * Checks if an item is the skills menu item (nether star).
     */
    private fun isSkillsItem(item: org.bukkit.inventory.ItemStack?): Boolean {
        if (item == null || item.type != Material.NETHER_STAR) return false
        val meta = item.itemMeta ?: return false
        val displayName = meta.displayName() ?: return false
        val plainText = PlainTextComponentSerializer.plainText().serialize(displayName)
        return plainText.contains("Skills", ignoreCase = true)
    }


    /**
     * Prevents spectators from picking up items.
     */
    @EventHandler(ignoreCancelled = true)
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        val entity = event.entity
        if (entity !is Player) return

        // Cancel pickup for spectators
        if (entity.gameMode == GameMode.SPECTATOR) {
            event.isCancelled = true
        }
    }
}
