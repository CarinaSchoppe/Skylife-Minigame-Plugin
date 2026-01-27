package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.skills.SkillsGui
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Listener for skills item interactions.
 * Opens the skills GUI when player right-clicks the skills item.
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
        val displayName = meta.displayName()

        // Check if display name contains "Skills"
        val plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(displayName)
        if (!plainText.contains("Skills", ignoreCase = true)) return

        // Open skills GUI
        event.isCancelled = true
        val gui = SkillsGui(event.player)
        gui.open()
    }
}
