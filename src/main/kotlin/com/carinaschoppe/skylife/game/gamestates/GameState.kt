package com.carinaschoppe.skylife.game.gamestates

import org.bukkit.entity.Player

/**
 * Defines the contract for a state within the game's lifecycle (e.g., Lobby, Ingame).
 */
interface GameState {


    /**
     * An enumeration of the possible game states, used for identification.
     * This is a legacy enum and might be replaced by the GameState interface's own State enum.
     *
     * @property id The numeric identifier for the game state.
     */
    enum class States(val id: Int) {
        LOBBY(0),
        INGAME(1),
        END(2)
    }


    /**
     * An enumeration of the possible high-level states a game can be in.
     */


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