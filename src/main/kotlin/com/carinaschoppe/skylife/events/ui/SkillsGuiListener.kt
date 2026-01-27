package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.utility.ui.SkillsGui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

/**
 * Listener for skills GUI interactions.
 */
class SkillsGuiListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        if (holder !is SkillsGui) return

        // Cancel all clicks to prevent item removal
        event.isCancelled = true

        if (event.whoClicked !is Player) return
        val clickedItem = event.currentItem ?: return

        // Find which skill was clicked
        val skill = findSkillByItem(clickedItem) ?: return

        // Handle skill selection
        holder.handleSkillClick(skill)
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is SkillsGui) {
            // Cancel all drags to prevent item manipulation
            event.isCancelled = true
        }
    }

    /**
     * Finds the skill enum corresponding to an ItemStack.
     */
    private fun findSkillByItem(item: ItemStack): Skill? {
        return Skill.entries.firstOrNull { it.material == item.type }
    }
}