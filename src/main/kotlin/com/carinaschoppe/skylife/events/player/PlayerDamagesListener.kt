package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.guild.GuildManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Listener to manage damage between entities, specifically involving players.
 *
 * This listener ensures that players can only deal and receive damage under specific conditions:
 * - Both the attacker and the victim must be in a game.
 * - The game must be in the `IngameState`.
 * - Spectators are immune to damage and cannot deal damage.
 * - Damage between players in different games is prevented.
 * - Damage involving players not in a game (e.g., in the hub) is also cancelled to prevent spawn-killing.
 * - Guild members cannot damage each other unless friendly fire is enabled or they're the last team standing.
 * - Party members can always damage each other (friendly fire is always enabled for parties).
 */
class PlayerDamagesListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val victim = event.entity
        val damager = event.damager

        // Check if either the damager or the victim is a protected player.
        val victimIsProtected = victim is Player && isProtected(victim)
        val damagerIsProtected = damager is Player && isProtected(damager)

        if (victimIsProtected || damagerIsProtected) {
            event.isCancelled = true
            return
        }

        // Additionally, prevent players from different games from damaging each other.
        if (victim is Player && damager is Player) {
            val victimGame = GameCluster.getGame(victim)
            val damagerGame = GameCluster.getGame(damager)

            // If they are in different games (and not in the hub, which is handled by isProtected),
            // cancel the event.
            if (victimGame != damagerGame) {
                event.isCancelled = true
                return
            }

            // Check guild friendly fire (parties can always damage each other)
            if (victimGame != null && damagerGame != null) {
                // Snapshot guild membership at time of damage check
                val damagerGuildId = GuildManager.getPlayerGuildId(damager.uniqueId)
                val victimGuildId = GuildManager.getPlayerGuildId(victim.uniqueId)

                // Only check friendly fire if both are in guilds AND the same guild
                if (damagerGuildId != null && victimGuildId != null && damagerGuildId == victimGuildId) {
                    // Check if friendly fire is enabled for the guild
                    val guild = GuildManager.getGuild(damagerGuildId)
                    if (guild != null && !guild.friendlyFireEnabled) {
                        // Check if guild is last team standing (only guild members alive)
                        val isLastTeam = GuildManager.isLastTeamStanding(damagerGuildId, victimGame.livingPlayers)
                        if (!isLastTeam) {
                            // Cancel damage - friendly fire is disabled and guild is not last team
                            event.isCancelled = true
                            return
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        val victim = event.entity

        // Protect all players who are not in an active game
        if (victim is Player && isProtected(victim)) {
            event.isCancelled = true
        }
    }

    /**
     * Checks if a player is protected from dealing or receiving damage.
     *
     * A player is considered protected if:
     * 1. They are not in a game (i.e., they are in the hub).
     * 2. They are in a game that is not currently in the `IngameState`.
     * 3. They are a spectator.
     * 4. They are in the protection phase (cooldown before PvP starts).
     *
     * @param player The player to check.
     * @return `true` if the player is protected, `false` otherwise.
     */
    private fun isProtected(player: Player): Boolean {
        val game = GameCluster.getGame(player)

        // Players in the hub are protected.
        if (game == null) {
            return true
        }

        // Players are protected if they are spectators
        if (game.spectators.contains(player)) {
            return true
        }

        // Check if game is in IngameState
        val state = game.currentState
        if (state !is IngameState) {
            return true
        }

        // Check if protection phase is active
        return state.protectionActive
    }
}