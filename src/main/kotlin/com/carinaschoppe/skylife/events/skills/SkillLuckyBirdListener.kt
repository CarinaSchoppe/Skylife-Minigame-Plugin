package com.carinaschoppe.skylife.events.skills

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Egg
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Listener for the Lucky Bird skill.
 * Applies random effects when lucky egg is thrown.
 */
class SkillLuckyBirdListener : Listener {

    private val randomEffects = listOf(
        PotionEffectType.SPEED,
        PotionEffectType.JUMP_BOOST,
        PotionEffectType.STRENGTH,
        PotionEffectType.RESISTANCE,
        PotionEffectType.REGENERATION,
        PotionEffectType.FIRE_RESISTANCE
    )

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val egg = event.entity as? Egg ?: return
        val player = egg.shooter as? Player ?: return

        if (!SkillsManager.hasSkillActive(player, Skill.LUCKY_BIRD)) return

        // Check if it's the lucky egg by checking custom name
        val item = player.inventory.itemInMainHand
        if (item.type != Material.EGG) return

        val meta = item.itemMeta ?: return
        val name = meta.displayName()?.let {
            PlainTextComponentSerializer.plainText()
                .serialize(it)
        }

        name?.contains("Lucky", ignoreCase = true)?.let { if (!it) return }

        // Apply random effect
        val effect = randomEffects.random()
        val duration = (100..300).random() // 5-15 seconds
        val amplifier = (0..1).random()

        player.addPotionEffect(
            PotionEffect(
                effect,
                duration,
                amplifier,
                false,
                true,
                true
            )
        )
    }
}
