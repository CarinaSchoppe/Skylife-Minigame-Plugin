package com.carinaschoppe.skylife.events.skills

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleFlightEvent
import java.util.*

/**
 * Listener for the Kangaroo skill.
 * Allows players to perform a double jump.
 */
class SkillKangarooListener : Listener {

    private val doubleJumpReady = mutableSetOf<UUID>()

    @EventHandler
    fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
        val player = event.player

        // Ignore if player is in creative or spectator mode
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return

        if (!SkillsManager.hasSkillActive(player, Skill.KANGAROO)) return

        // Cancel the flight toggle
        event.isCancelled = true

        // Perform double jump
        player.allowFlight = false
        player.isFlying = false

        val velocity = player.velocity
        velocity.y = 0.8 // Double jump strength
        player.velocity = velocity

        doubleJumpReady.remove(player.uniqueId)
    }

    @EventHandler
    fun onPlayerMove(event: org.bukkit.event.player.PlayerMoveEvent) {
        val player = event.player

        // Ignore if player is in creative or spectator mode
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return

        if (!SkillsManager.hasSkillActive(player, Skill.KANGAROO)) return

        // Enable flight (double jump) when player is on ground
        if (isPlayerOnGround(player)) {
            player.allowFlight = true
            doubleJumpReady.add(player.uniqueId)
        }
    }

    private fun isPlayerOnGround(player: Player): Boolean {
        val blockBelow = player.location.clone().subtract(0.0, 0.1, 0.0).block
        return blockBelow.type.isSolid
    }
}
