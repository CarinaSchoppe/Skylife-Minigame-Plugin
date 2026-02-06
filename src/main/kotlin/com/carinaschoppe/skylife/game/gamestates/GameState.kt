package com.carinaschoppe.skylife.game.gamestates

/**
 * Defines the contract for a state within the game's lifecycle (e.g., Lobby, Ingame).
 */
interface GameState<P> {

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
    fun playerJoined(player: P)

    /**
     * Handles logic for when a player leaves the game during this state.
     *
     * @param player The player who left.
     */
    fun playerLeft(player: P)
}
