package com.carinaschoppe.skylife.game.gamestates

/**
 * An enumeration of the possible game states, used for identification.
 * This is a legacy enum and might be replaced by the GameState interface's own State enum.
 *
 * @property id The numeric identifier for the game state.
 */
enum class GameStateType(val id: Int) {
    /** Lobby state - players are waiting for the game to start */
    LOBBY(0),

    /** Ingame state - the game is actively being played */
    INGAME(1),

    /** End state - the game has concluded */
    END(2)
}
