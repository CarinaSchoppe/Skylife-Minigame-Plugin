package com.carinaschoppe.skylife.events.skills

import com.carinaschoppe.skylife.platform.PluginContext
import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * Listener for the Pilot skill.
 * Allows players to fly for 3 seconds, then gives nausea for 3 seconds.
 */
class SkillPilotListener : Listener {

    private val cooldowns = mutableMapOf<UUID, Long>()
    private val activeFlyers = mutableSetOf<UUID>()
    private val cooldownTime = 15000L // 15 seconds cooldown

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (!SkillsManager.hasSkillActive(player, Skill.PILOT)) return

        // Check if player right-clicked
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        // Check if player is holding an elytra (pilot item)
        val item = event.item
        if (item == null || item.type != Material.ELYTRA) return

        // Check if it's the pilot wings item
        val itemName = item.itemMeta?.displayName()
        if (itemName == null || !itemName.toString().contains("Pilot Wings")) return

        // Check if already flying with pilot skill
        if (activeFlyers.contains(player.uniqueId)) {
            player.sendMessage("§cYou are already using the Pilot skill!")
            return
        }

        // Check cooldown
        val now = System.currentTimeMillis()
        val lastUse = cooldowns[player.uniqueId] ?: 0L
        if (now - lastUse < cooldownTime) {
            val remaining = (cooldownTime - (now - lastUse)) / 1000
            player.sendMessage("§cPilot skill on cooldown! ${remaining}s remaining")
            return
        }

        // Start flight sequence
        startFlightSequence(player)

        // Set cooldown
        cooldowns[player.uniqueId] = now

        event.isCancelled = true
    }

    private fun startFlightSequence(player: Player) {
        activeFlyers.add(player.uniqueId)

        // Enable flight
        player.allowFlight = true
        player.isFlying = true
        player.flySpeed = 0.05f // Slow flight speed

        player.sendMessage("§aYou can now fly for 3 seconds!")

        // Schedule flight end after 3 seconds (60 ticks)
        object : BukkitRunnable() {
            override fun run() {
                // Disable flight
                if (player.gameMode != GameMode.CREATIVE && player.gameMode != GameMode.SPECTATOR) {
                    player.allowFlight = false
                    player.isFlying = false
                    player.flySpeed = 0.1f // Reset to default
                }

                // Apply nausea for 3 seconds (60 ticks)
                player.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.NAUSEA,
                        60, // 3 seconds
                        0,
                        false,
                        false,
                        true
                    )
                )

                player.sendMessage("§cYou feel nauseous after flying!")

                // Remove from active flyers after nausea ends
                object : BukkitRunnable() {
                    override fun run() {
                        activeFlyers.remove(player.uniqueId)
                    }
                }.runTaskLater(PluginContext.plugin, 60L) // After 3 seconds
            }
        }.runTaskLater(PluginContext.plugin, 60L) // After 3 seconds of flight
    }
}
