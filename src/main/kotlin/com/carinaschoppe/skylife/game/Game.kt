package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.gamestates.LobbyState
import com.carinaschoppe.skylife.game.managers.MapManager
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * Represents a single minigame instance, managing its state, players, and settings.
 *
 * This class serves as the core container for a game session, handling player management,
 * state transitions, and game-specific data. Each game operates independently with its
 * own lifecycle and player list.
 *
 * @property name The unique name identifier for this game instance.
 * @property minPlayers The minimum number of players required to start the game.
 * @property maxPlayers The maximum number of players allowed in the game.
 * @property lobbyLocation The location where players wait before the game starts.
 * @property ingameLocation The location where players are teleported when the game begins.
 * @property mapName The display name of the map being used for this game.
 * @property pattern The [GamePattern] template this game is based on.
 */
class Game(
    val name: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val lobbyLocation: Location,
    val ingameLocation: Location,
    val mapName: String,
    val pattern: GamePattern
) {
    /**
     * A unique identifier for this game instance.
     *
     * This ID is automatically generated when the game is created and can be used
     * to uniquely identify this game instance across the server.
     */
    val gameID: UUID = UUID.randomUUID()

    /**
     * List of players who are currently spectating this game.
     *
     * Spectators can observe the game but cannot participate until they join as active players.
     */
    val spectators = mutableListOf<Player>()

    /**
     * The current state handler for this game.
     *
     * This property holds the current state object that manages the game's behavior.
     * The state determines what actions are allowed and how the game responds to events.
     * Defaults to [LobbyState] when the game is first created.
     */
    var currentState: GameState = LobbyState(this)

    /**
     * A list of players who are currently alive and participating in the game.
     *
     * This list is managed by the game's state handlers and is used to track
     * active participants. Players are removed when they are eliminated or leave.
     */
    val livingPlayers = mutableListOf<Player>()

    /**
     * Tracks the number of kills each player has achieved in the current game session.
     *
     * The map uses player UUIDs as keys and their kill counts as values.
     * This data is typically reset when the game restarts.
     */
    val gameKills = mutableMapOf<UUID, Int>()

    /**
     * The high-level state of the game.
     *
     * This property represents the current phase of the game's lifecycle:
     * - LOBBY: Players are joining, waiting to start
     * - INGAME: The game is actively being played
     * - END: The game is finishing up
     * - PROTECT: A protected state (e.g., grace period)
     */
    var state: GameState.States = GameState.States.LOBBY

    /**
     * Starts the game by initializing the current state.
     *
     * This method triggers the [GameState.start] method of the current state,
     * beginning the game's lifecycle. The actual behavior depends on the
     * current state implementation.
     */
    fun start() {
        // Load the game world before starting the state
        MapManager.loadGameWorld(this)
        currentState.start()
    }

    /**
     * Stops the game and cleans up the current state.
     *
     * This method triggers the [GameState.stop] method of the current state,
     * allowing for proper cleanup and state transition. The game can be
     * restarted by calling [start] again.
     */
    fun stop() {
        try {
            currentState.stop()
        } finally {
            // Always attempt to unload the world, even if stop() throws an exception
            MapManager.unloadWorld(this)
        }
    }

    /**
     * Broadcasts a message to all players in the game.
     *
     * This helper method sends a message to both living players and spectators.
     *
     * @param message The message to broadcast.
     */
    /**
     * Gets all players currently in the game, including both living players and spectators.
     *
     * @return A list containing all players in the game.
     */
    fun getAllPlayers(): List<Player> {
        return livingPlayers + spectators
    }

    /**
     * Broadcasts a message to all players in the game, including spectators.
     *
     * @param message The message to broadcast.
     */
    fun broadcast(message: String) {
        broadcast(Component.text(message))
    }
    /* <<<<<<<<<<  28f68cf2-e4d0-43ff-b4e8-90799a5c3286  >>>>>>>>>>> */
    /**
     * Broadcasts a message to all players in the game, including spectators.
     *
     * @param message The message to broadcast. Type @see Component
     */
    fun broadcast(message: Component) {
        getAllPlayers().forEach { it.sendMessage(message) }
    }
}