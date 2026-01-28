package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.utility.ui.SkillsGui
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Listener for skills item interactions.
 * Opens the skills GUI when player right-clicks the skills item.
 * Skills can only be changed in Lobby state or Hub (not during active gameplay).
 */
class PlayerSkillsItemListener : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val item = event.item ?: return
        if (item.type != Material.NETHER_STAR) return

        // Check if it's the skills item by checking display name
        val meta = item.itemMeta ?: return
        val displayName = meta.displayName() ?: return

        // Check if display name contains "Skills"
        val plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(displayName)
        if (!plainText.contains("Skills", ignoreCase = true)) return

        event.isCancelled = true
        val player = event.player

        // Check if player is in a game
        val game = GameCluster.getGamePlayerIsIn(player)

        // Only allow skills GUI in Lobby state or Hub (not in InGame/End states)
        if (game != null && game.currentState !is com.carinaschoppe.skylife.game.gamestates.LobbyState) {
            // Player is in InGame or End state - don't allow changing skills
            return
        }

        // Open skills GUI
        val gui = SkillsGui(player)
        gui.open()
    }
}
