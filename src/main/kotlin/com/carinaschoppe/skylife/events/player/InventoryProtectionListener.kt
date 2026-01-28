package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.EndState
import com.carinaschoppe.skylife.game.gamestates.LobbyState
import com.carinaschoppe.skylife.hub.HubManager
import com.carinaschoppe.skylife.utility.ui.ExitDoorItem
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
import com.carinaschoppe.skylife.utility.ui.SkillsGui
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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
     * Prevents dropping of special items in lobby, hub, and end states.
     */
    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        val player = event.player

        // Check if player is in hub (not in game)
        val game = GameCluster.getGameByPlayer(player)
        val isInHub = game == null

        // Prevent dropping special items when in lobby, hub, or end state
        if (isInHub || game?.currentState is LobbyState || game?.currentState is EndState) {
            if (GameOverviewItems.isMenuItem(item) ||
                SkillsGui.createSkillsMenuItem().isSimilar(item) ||
                ExitDoorItem.isExitDoor(item)
            ) {
                event.isCancelled = true
            }
        }
    }

    /**
     * Prevents moving special items in inventory when in lobby, hub, or end states.
     */
    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? org.bukkit.entity.Player ?: return
        val currentItem = event.currentItem
        val cursor = event.cursor

        val game = GameCluster.getGameByPlayer(player)
        val isInHub = game == null

        // Prevent moving special items when in lobby, hub, or end state
        if (isInHub || game?.currentState is LobbyState || game?.currentState is EndState) {
            val isProtectedItem = GameOverviewItems.isMenuItem(currentItem) ||
                    GameOverviewItems.isMenuItem(cursor) ||
                    SkillsGui.createSkillsMenuItem().isSimilar(currentItem) ||
                    SkillsGui.createSkillsMenuItem().isSimilar(cursor) ||
                    ExitDoorItem.isExitDoor(currentItem) ||
                    ExitDoorItem.isExitDoor(cursor)

            if (isProtectedItem) {
                event.isCancelled = true
            }
        }
    }

    /**
     * Prevents dragging special items when in lobby, hub, or end states.
     */
    @EventHandler(ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? org.bukkit.entity.Player ?: return
        val item = event.oldCursor

        val game = GameCluster.getGameByPlayer(player)
        val isInHub = game == null

        // Prevent dragging special items when in lobby, hub, or end state
        if (isInHub || game?.currentState is LobbyState || game?.currentState is EndState) {
            if (GameOverviewItems.isMenuItem(item) ||
                SkillsGui.createSkillsMenuItem().isSimilar(item) ||
                ExitDoorItem.isExitDoor(item)
            ) {
                event.isCancelled = true
            }
        }
    }

    /**
     * Handles clicking the exit door to teleport to hub.
     */
    @EventHandler(ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (!ExitDoorItem.isExitDoor(item)) {
            return
        }

        event.isCancelled = true
        val player = event.player

        // Execute hub command - teleport player to hub
        HubManager.teleportToHub(player)

        // Set to adventure mode
        player.gameMode = GameMode.ADVENTURE

        // Clear inventory when leaving
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4)

        // Give them hub items
        player.inventory.setItem(4, SkillsGui.createSkillsMenuItem())
        player.inventory.setItem(8, GameOverviewItems.createMenuItem())
    }
}
