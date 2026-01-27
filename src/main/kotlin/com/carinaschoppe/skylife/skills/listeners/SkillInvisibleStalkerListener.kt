package com.carinaschoppe.skylife.skills.listeners

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Listener for the Invisible Stalker skill.
 * Grants invisibility when player starts sneaking.
 */
class SkillInvisibleStalkerListener : Listener {

    @EventHandler
    fun onPlayerToggleSneak(event: PlayerToggleSneakEvent) {
        val player = event.player

        if (!SkillsManager.hasSkillActive(player, Skill.INVISIBLE_STALKER)) return

        if (event.isSneaking) {
            // Player started sneaking - give invisibility
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    999999,
                    0,
                    false,
                    false,
                    false
                )
            )
        } else {
            // Player stopped sneaking - remove invisibility
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
        }
    }
}
