package com.carinaschoppe.skylife.utility.configuration

/**
 * A configuration class that defines various timing constants used throughout the plugin.
 *
 * This class follows the singleton pattern to provide consistent timing values
 * across different parts of the plugin. It contains predefined durations (in seconds)
 * for different game phases and mechanics.
 *
 * @property LOBBY_TIMER The duration (in seconds) players wait in the lobby before the game starts. Default: 60 seconds
 * @property INGAME_TIMER The maximum duration (in seconds) of a game round. Default: 900 seconds (15 minutes)
 * @property END_TIMER The duration (in seconds) of the end-game phase before returning to the lobby. Default: 10 seconds
 * @property PROTECTION_TIMER The duration (in seconds) of the initial spawn protection. Default: 10 seconds
 */
class Timer {

    companion object {
        /**
         * The singleton instance of the Timer.
         */
        lateinit var instance: Timer
    }

    /** Duration (in seconds) for the lobby countdown before the game starts. */
    var LOBBY_TIMER = 60

    /** Maximum duration (in seconds) for an active game session. */
    var INGAME_TIMER = 60 * 15  // 15 minutes

    /** Duration (in seconds) for the end-game phase before returning to lobby. */
    var END_TIMER = 10

    /** Duration (in seconds) of spawn protection after game starts (PvP cooldown phase). */
    var PROTECTION_TIMER = 15
}