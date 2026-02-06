package com.carinaschoppe.skylife.utility

import com.carinaschoppe.skylife.platform.PluginContext
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

/**
 * Manages vanish functionality for players.
 * Allows administrators and moderators to become invisible to other players.
 *
 * Features:
 * - Complete invisibility (player list, tab list, and visibility)
 * - Automatic fly mode when vanished
 * - State restoration when unvanished
 * - Memory-safe with proper cleanup
 * - Permission-based visibility control
 *
 * Security measures:
 * - Uses Paper API for proper player hiding
 * - Prevents memory leaks by cleaning up on quit
 * - Prevents vanished players from joining games
 * - Auto-unvanish on death to prevent exploits
 * - Tab completion doesn't leak vanished players
 *
 * Thread-safety: This object is accessed from the main server thread only.
 */
object VanishManager {

    /**
     * Set of UUIDs of players who are currently vanished.
     * Using UUID instead of Player to prevent memory leaks.
     */
    private val vanishedPlayers = mutableSetOf<UUID>()

    /**
     * Stores the previous game mode of vanished players for restoration.
     */
    private val previousGameModes = mutableMapOf<UUID, GameMode>()

    /**
     * Stores the previous fly state (isFlying) of vanished players.
     */
    private val previousFlyStates = mutableMapOf<UUID, Boolean>()

    /**
     * Stores the previous allowFlight state of vanished players.
     */
    private val previousAllowFlightStates = mutableMapOf<UUID, Boolean>()

    /**
     * Checks if a player is currently vanished.
     *
     * @param player The player to check
     * @return true if the player is vanished, false otherwise
     */
    fun isVanished(player: Player): Boolean {
        return vanishedPlayers.contains(player.uniqueId)
    }

    /**
     * Checks if a player UUID is currently vanished.
     *
     * @param uuid The player UUID to check
     * @return true if the player is vanished, false otherwise
     */
    fun isVanished(uuid: UUID): Boolean {
        return vanishedPlayers.contains(uuid)
    }

    /**
     * Toggles vanish state for a player.
     * If the player is vanished, they will be unvanished.
     * If the player is not vanished, they will be vanished.
     *
     * @param player The player to toggle vanish for
     * @return true if the player is now vanished, false if they are now visible
     */
    fun toggleVanish(player: Player): Boolean {
        return if (isVanished(player)) {
            unvanish(player)
            false
        } else {
            vanish(player)
            true
        }
    }

    /**
     * Vanishes a player, making them invisible to other players.
     *
     * Effects:
     * - Hides player from all players without skylife.vanish.see permission
     * - Enables fly mode
     * - Stores previous states for restoration
     *
     * Security:
     * - Uses Paper API with plugin reference for proper hiding
     * - Only hides from players without permission
     *
     * @param player The player to vanish
     */
    fun vanish(player: Player) {
        if (isVanished(player)) return

        vanishedPlayers.add(player.uniqueId)

        // Store previous states BEFORE changing anything
        previousGameModes[player.uniqueId] = player.gameMode
        previousFlyStates[player.uniqueId] = player.isFlying
        previousAllowFlightStates[player.uniqueId] = player.allowFlight

        // Hide player from all other players (Paper API with plugin reference)
        val plugin = PluginContext.plugin
        Bukkit.getOnlinePlayers().forEach { other ->
            if (other.uniqueId != player.uniqueId && !other.hasPermission("skylife.vanish.see")) {
                other.hidePlayer(plugin, player)
            }
        }

        // Enable fly mode
        player.allowFlight = true
        player.isFlying = true
    }

    /**
     * Unvanishes a player, making them visible to all players again.
     *
     * Effects:
     * - Shows player to all other players
     * - Restores previous fly state (unless in creative/spectator)
     * - Cleans up stored states
     *
     * State restoration logic:
     * - CREATIVE/SPECTATOR: Keep flying enabled
     * - SURVIVAL/ADVENTURE: Restore previous fly state
     *
     * @param player The player to unvanish
     */
    fun unvanish(player: Player) {
        if (!isVanished(player)) return

        vanishedPlayers.remove(player.uniqueId)

        // Show player to all other players (Paper API with plugin reference)
        val plugin = PluginContext.plugin
        Bukkit.getOnlinePlayers().forEach { other ->
            other.showPlayer(plugin, player)
        }

        // Restore previous states
        val previousGameMode = previousGameModes.remove(player.uniqueId)
        val previousFlyState = previousFlyStates.remove(player.uniqueId)
        val previousAllowFlightState = previousAllowFlightStates.remove(player.uniqueId)

        // Only restore fly state if player is NOT in creative/spectator mode
        // In those modes, flying is always allowed
        if (previousGameMode != null) {
            when (previousGameMode) {
                GameMode.CREATIVE, GameMode.SPECTATOR -> {
                    // Keep flying enabled for these modes
                    player.allowFlight = true
                }

                else -> {
                    // Restore previous fly state for survival/adventure
                    player.allowFlight = previousAllowFlightState ?: false
                    if (!player.allowFlight) {
                        player.isFlying = false
                    } else {
                        player.isFlying = previousFlyState ?: false
                    }
                }
            }
        }
    }

    /**
     * Unvanishes all currently vanished players.
     * Useful for plugin disable or emergency situations.
     */
    fun unvanishAll() {
        vanishedPlayers.toList().forEach { uuid ->
            Bukkit.getPlayer(uuid)?.let { player ->
                unvanish(player)
            }
        }
    }

    /**
     * Handles player join event.
     * Hides all vanished players from the joining player.
     *
     * Security:
     * - Only hides vanished players from players without skylife.vanish.see permission
     * - Uses Paper API for proper hiding
     *
     * @param player The player who joined
     */
    fun handlePlayerJoin(player: Player) {
        // New joining player should not see vanished players
        val plugin = PluginContext.plugin
        vanishedPlayers.mapNotNull { Bukkit.getPlayer(it) }.forEach { vanished ->
            if (!player.hasPermission("skylife.vanish.see")) {
                player.hidePlayer(plugin, vanished)
            }
        }
    }

    /**
     * Handles player quit event.
     * Unvanishes the player and cleans up all stored data.
     *
     * Security:
     * - Prevents vanished state from persisting across sessions
     * - Prevents memory leaks by cleaning up all stored data
     * - Shows player to all other players before they leave
     *
     * @param player The player who is quitting
     */
    fun handlePlayerQuit(player: Player) {
        // Clean up vanish state and ALL stored data when player quits
        if (isVanished(player)) {
            val uuid = player.uniqueId
            vanishedPlayers.remove(uuid)

            // Clean up all stored states to prevent memory leak
            previousGameModes.remove(uuid)
            previousFlyStates.remove(uuid)
            previousAllowFlightStates.remove(uuid)

            // Show player to everyone (cleanup)
            val plugin = PluginContext.plugin
            Bukkit.getOnlinePlayers().forEach { other ->
                if (other.uniqueId != uuid) {
                    other.showPlayer(plugin, player)
                }
            }
        }
    }

    /**
     * Gets a copy of all vanished player UUIDs.
     * Returns a copy to prevent external modification.
     *
     * @return Set of UUIDs of all vanished players
     */
    fun getVanishedPlayers(): Set<UUID> {
        return vanishedPlayers.toSet()
    }
}
