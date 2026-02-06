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

        when {
            victim is Player && damager is Player && shouldCancelPlayerDamage(victim, damager) -> {
                event.isCancelled = true
            }

            victim is Player && isProtected(victim) -> {
                event.isCancelled = true
            }

            damager is Player && isProtected(damager) -> {
                event.isCancelled = true
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

    private fun shouldCancelPlayerDamage(victim: Player, damager: Player): Boolean {
        if (isProtected(victim) || isProtected(damager)) {
            return true
        }

        val victimGame = GameCluster.getGame(victim)
        val damagerGame = GameCluster.getGame(damager)
        if (victimGame != damagerGame) {
            return true
        }

        if (victimGame == null) {
            return false
        }

        return isGuildFriendlyFireBlocked(victimGame, damager, victim)
    }

    private fun isGuildFriendlyFireBlocked(
        game: com.carinaschoppe.skylife.game.Game,
        damager: Player,
        victim: Player
    ): Boolean {
        val damagerGuildId = GuildManager.getPlayerGuildId(damager.uniqueId)
        val victimGuildId = GuildManager.getPlayerGuildId(victim.uniqueId)

        if (damagerGuildId == null || victimGuildId == null || damagerGuildId != victimGuildId) {
            return false
        }

        val guild = GuildManager.getGuild(damagerGuildId) ?: return false
        if (guild.friendlyFireEnabled) {
            return false
        }

        val isLastTeam = GuildManager.isLastTeamStanding(damagerGuildId, game.livingPlayers)
        return !isLastTeam
    }
}
