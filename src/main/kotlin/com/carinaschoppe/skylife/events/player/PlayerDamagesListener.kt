package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.guild.GuildManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

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

            // Check guild friendly fire
            if (victimGame != null && damagerGame != null) {
                // Check if they're in the same guild
                if (GuildManager.areInSameGuild(damager.uniqueId, victim.uniqueId)) {
                    val guildId = GuildManager.getPlayerGuildId(damager.uniqueId)
                    if (guildId == null) return // Safety check

                    // Check if friendly fire is enabled for the guild
                    val guild = GuildManager.getGuild(guildId)
                    if (guild != null && !guild.friendlyFireEnabled) {
                        // Check if guild is last team standing (only guild members alive)
                        val isLastTeam = GuildManager.isLastTeamStanding(guildId, victimGame.livingPlayers)
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

    /**
     * Checks if a player is protected from dealing or receiving damage.
     *
     * A player is considered protected if:
     * 1. They are not in a game (i.e., they are in the hub).
     * 2. They are in a game that is not currently in the `IngameState`.
     * 3. They are a spectator.
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

        // Players are protected if the game is not in progress or if they are spectators.
        return game.currentState !is IngameState || game.spectators.contains(player)
    }
}