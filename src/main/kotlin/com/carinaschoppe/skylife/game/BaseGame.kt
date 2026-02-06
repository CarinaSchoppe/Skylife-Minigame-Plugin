package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.gamestates.GameStateType
import net.kyori.adventure.text.Component
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
class BaseGame<P, L, W>(
    val name: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val lobbyLocation: L,
    val ingameLocation: L,
    val mapName: String,
    val pattern: GamePattern,
    stateFactory: (BaseGame<P, L, W>) -> GameState<P>,
    private val messageSender: (P, Component) -> Unit
) {
    /**
     * A unique identifier for this game instance.
     *
     * This ID is automatically generated when the game is created and can be used
     * to uniquely identify this game instance across the server.
     */
    val gameID: UUID = UUID.randomUUID()

    /**
     * The dedicated world for this game instance.
     * Loaded when the game is created and cleaned up when it ends.
     */
    var gameWorld: W? = null
        private set

    /**
     * List of players who are currently spectating this game.
     *
     * Spectators can observe the game but cannot participate until they join as active players.
     */
    val spectators = mutableListOf<P>()

    /**
     * The current state handler for this game.
     *
     * This property holds the current state object that manages the game's behavior.
     * The state determines what actions are allowed and how the game responds to events.
     * Defaults to [LobbyState] when the game is first created.
     */
    var currentState: GameState<P> = stateFactory(this)

    /**
     * A list of players who are currently alive and participating in the game.
     *
     * This list is managed by the game's state handlers and is used to track
     * active participants. Players are removed when they are eliminated or leave.
     */
    val livingPlayers = mutableListOf<P>()

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
    var state: GameStateType = GameStateType.LOBBY

    /**
     * Starts the game by initializing the current state.
     *
     * This method triggers the [GameState.start] method of the current state,
     * beginning the game's lifecycle. The actual behavior depends on the
     * current state implementation.
     */
    fun start() {
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
        currentState.stop()
    }

    /**
     * Gets all players currently in the game, including both living players and spectators.
     *
     * @return A list containing all players in the game.
     */
    fun getAllPlayers(): List<P> {
        return livingPlayers + spectators
    }

    /**
     * Broadcasts a message to all players in the game, including spectators.
     *
     * @param message The message to broadcast. Type @see Component
     */
    fun broadcast(message: Component) {
        getAllPlayers().forEach { player -> messageSender(player, message) }
    }

    internal fun attachGameWorld(world: W?) {
        gameWorld = world
    }
}
