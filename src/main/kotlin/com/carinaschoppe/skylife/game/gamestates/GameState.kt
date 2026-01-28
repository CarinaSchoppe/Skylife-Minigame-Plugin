package com.carinaschoppe.skylife.game.gamestates

import org.bukkit.entity.Player

/**
 * Defines the contract for a state within the game's lifecycle (e.g., Lobby, Ingame).
 */
interface GameState {

    /**
     * Called when this state becomes the active state.
     */
    fun start()

    /**
     * Called when this state is being transitioned away from.
     */
    fun stop()

    /**
     * Handles logic for when a player joins the game during this state.
     *
     * @param player The player who joined.
     */
    fun playerJoined(player: Player)

    /**
     * Handles logic for when a player leaves the game during this state.
     *
     * @param player The player who left.
     */
    fun playerLeft(player: Player)
}