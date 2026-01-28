package com.carinaschoppe.skylife.events.skills

import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillsManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

/**
 * Listener for the Kletterer skill.
 * Allows players to climb walls when looking at them and moving forward.
 */
class SkillKlettererListener : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player

        if (!SkillsManager.hasSkillActive(player, Skill.KLETTERER)) return

        // Check if player is against a wall
        if (!isAgainstWall(player)) return

        // Check if player is moving forward
        val velocity = player.velocity
        if (velocity.lengthSquared() < 0.01) return

        // Check if player is looking at a wall (pitch between -45 and 45 degrees)
        val pitch = player.location.pitch
        if (pitch < -45 || pitch > 45) return

        // Apply upward velocity to climb
        val climbVelocity = Vector(0.0, 0.3, 0.0)
        player.velocity = climbVelocity
    }

    private fun isAgainstWall(player: Player): Boolean {
        val location = player.location
        val direction = location.direction.normalize()

        // Check block in front of player
        val blockInFront = location.clone().add(direction.multiply(0.5)).block
        return blockInFront.type.isSolid && blockInFront.type != Material.AIR
    }
}
