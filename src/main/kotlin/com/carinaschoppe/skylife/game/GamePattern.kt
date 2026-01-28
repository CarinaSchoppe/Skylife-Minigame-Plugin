package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.managers.GameLocationManager

/**
 * Represents a template or blueprint for creating game instances.
 *
 * A GamePattern defines the configuration and layout of a specific game map,
 * including player limits, spawn points, and other location-based settings.
 * Multiple game instances can be created from a single pattern.
 *
 * @property mapName The unique identifier for this game pattern/map
 * @property minPlayers The minimum number of players required to start a game
 * @property maxPlayers The maximum number of players allowed in a game
 * @property gameLocationManager Manages all location-related settings for this pattern
 */
class GamePattern(val mapName: String) {

    /**
     * The minimum number of players required to start a game using this pattern.
     * Defaults to 0, which typically means no minimum is enforced.
     */
    var minPlayers: Int = 0

    /**
     * The maximum number of players allowed in a game using this pattern.
     * Defaults to 0, which typically means no limit is set.
     */
    var maxPlayers: Int = 0

    /**
     * The minimum number of players needed to start the countdown/game.
     * Defaults to minPlayers if not set.
     */
    var minPlayersToStart: Int = 2

    /**
     * Manages all location-related settings for this game pattern,
     * including spawn points, lobby location, and other important positions.
     */
    val gameLocationManager = GameLocationManager()

    /**
     * Checks if this game pattern is fully configured and ready to be used.
     *
     * @return `true` if all required locations are set and player limits are valid,
     *         `false` otherwise
     */
    fun isComplete(): Boolean {
        return gameLocationManager.gamePatternComplete() &&
                minPlayers >= 0 &&
                maxPlayers >= minPlayers &&
                mapName.isNotBlank()
    }
}