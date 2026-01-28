package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.utility.ui.SkillPurchaseConfirmGui
import com.carinaschoppe.skylife.utility.ui.SkillsGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

/**
 * Listener for skills GUI interactions.
 * Handles both the skills selection GUI and the skill purchase confirmation GUI.
 */
class SkillsGuiListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder

        // Handle SkillsGui clicks
        if (holder is SkillsGui) {
            event.isCancelled = true

            if (event.whoClicked !is Player) return
            val clickedItem = event.currentItem ?: return

            // Find which skill was clicked
            val skill = findSkillByItem(clickedItem) ?: return

            // Handle skill selection
            holder.handleSkillClick(skill)
            return
        }

        // Handle SkillPurchaseConfirmGui clicks
        if (holder is SkillPurchaseConfirmGui) {
            event.isCancelled = true

            if (event.whoClicked !is Player) return
            val clickedItem = event.currentItem ?: return

            when (clickedItem.type) {
                Material.LIME_STAINED_GLASS_PANE -> holder.handleConfirm()
                Material.RED_STAINED_GLASS_PANE -> holder.handleCancel()
                else -> return
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is SkillsGui || holder is SkillPurchaseConfirmGui) {
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