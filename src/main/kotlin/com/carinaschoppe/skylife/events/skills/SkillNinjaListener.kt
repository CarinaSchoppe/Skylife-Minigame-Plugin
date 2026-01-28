package com.carinaschoppe.skylife.events.skills

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

/**
 * Listener for the Ninja skill.
 * Allows players to become invisible for a short time by right-clicking with a specific item.
 */
class SkillNinjaListener : Listener {

    private val cooldowns = mutableMapOf<UUID, Long>()
    private val cooldownTime = 30000L // 30 seconds cooldown

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (!SkillsManager.hasSkillActive(player, Skill.NINJA)) return

        // Check if player right-clicked
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        // Check if player is holding a gray dye (ninja item)
        val item = event.item
        if (item == null || item.type != Material.GRAY_DYE) return

        // Check if it's the ninja cloak item
        val itemName = item.itemMeta?.displayName()
        if (itemName == null || !itemName.toString().contains("Ninja Cloak")) return

        // Check cooldown
        val now = System.currentTimeMillis()
        val lastUse = cooldowns[player.uniqueId] ?: 0L
        if (now - lastUse < cooldownTime) {
            val remaining = (cooldownTime - (now - lastUse)) / 1000
            player.sendMessage("§cNinja skill on cooldown! ${remaining}s remaining")
            return
        }

        // Apply invisibility for a short time
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.INVISIBILITY,
                100, // 5 seconds (100 ticks)
                0,
                false,
                false,
                true
            )
        )

        player.sendMessage("§7You became invisible for 5 seconds!")

        // Set cooldown
        cooldowns[player.uniqueId] = now

        // Remove one item if not in creative mode
        if (player.gameMode != org.bukkit.GameMode.CREATIVE) {
            item.amount -= 1
        }

        event.isCancelled = true
    }
}
