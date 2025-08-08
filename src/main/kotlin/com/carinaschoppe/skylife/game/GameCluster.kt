package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.gamestates.LobbyState
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.utility.scoreboard.ScoreboardManager
import org.bukkit.entity.Player

/**
 * Manages the entire collection of games, handling player joining, leaving, and game state transitions.
 */
object GameCluster {

    private val lobbyGames = mutableListOf<Game>()
    private val activeGames = mutableListOf<Game>()

    /**
     * Get a read-only list of all currently active games.
     */
    val activeGamesList: List<Game>
        get() = activeGames.toList()

    /**
     * Collection of game patterns that serve as templates for creating new games.
     */
    val gamePatterns = mutableListOf<GamePattern>()

    /**
     * Creates a new game instance from a pattern.
     *
     * @param pattern The game pattern to use as a template.
     * @return The created game instance.
     */
    fun createGameFromPattern(pattern: GamePattern): Game {
        val game = Game(
            name = pattern.mapName,
            minPlayers = pattern.minPlayers,
            maxPlayers = pattern.maxPlayers,
            lobbyLocation = GameLocationManager.skylifeLocationToLocationConverter(pattern.gameLocationManager.lobbyLocation),
            ingameLocation = GameLocationManager.skylifeLocationToLocationConverter(pattern.gameLocationManager.mainLocation),
            mapName = pattern.mapName,
            pattern = pattern
        )
        lobbyGames.add(game)
        return game
    }

    /**
     * Adds a new game to the cluster, placing it in the lobby state.
     *
     * @param game The game to add.
     */
    fun addGame(game: Game) {
        lobbyGames.add(game)
    }

    /**
     * Adds a player to a specific game.
     *
     * @param player The player to add.
     * @param game The game to join.
     */
    fun addPlayerToGame(player: Player, game: Game) {
        game.livingPlayers.add(player)
        player.teleport(game.lobbyLocation)
        game.currentState.playerJoined(player)
        ScoreboardManager.setScoreboard(player, game)
    }

    /**
     * Removes a player from the game they are currently in.
     *
     * @param player The player to remove.
     */
    fun removePlayerFromGame(player: Player) {
        val game = getGamePlayerIsIn(player) ?: return

        game.livingPlayers.remove(player)
        game.currentState.playerLeft(player)
        ScoreboardManager.removeScoreboard(player)
        KitManager.removePlayer(player)

        if (game.state == GameState.States.INGAME && game.livingPlayers.size <= 1) {
            game.stop()
        }
    }

    /**
     * Starts a game, moving it from the lobby list to the active list.
     *
     * @param game The game to start.
     */
    fun startGame(game: Game) {
        game.state = GameState.States.INGAME
        lobbyGames.remove(game)
        activeGames.add(game)
        game.start()
    }

    /**
     * Stops a game, moving it back to the lobby list and resetting it.
     *
     * @param game The game to stop.
     */
    fun stopGame(game: Game) {
        game.livingPlayers.forEach { player ->
            ScoreboardManager.removeScoreboard(player)
            KitManager.removePlayer(player)
            // Potentially teleport them back to a server lobby
        }
        game.livingPlayers.clear()

        game.state = GameState.States.LOBBY
        activeGames.remove(game)
        lobbyGames.add(game)
        game.currentState = LobbyState(game) // Reset to a fresh lobby state
    }

    /**
     * Finds a random, available game for a player to join.
     *
     * @return An available [Game], or null if none are found.
     */
    fun findRandomAvailableGame(): Game? {
        return lobbyGames.firstOrNull { it.livingPlayers.size < it.maxPlayers }
    }

    /**
     * Finds a game by its unique name.
     *
     * @param name The name of the game to find.
     * @return The found [Game], or null if no game with that name exists.
     */
    fun getGameByName(name: String): Game? {
        return lobbyGames.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: activeGames.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Retrieves the game that a specific player is currently in.
     *
     * @param player The player to search for.
     * @return The [Game] the player is in, or null if they are not in any game.
     */
    fun getGamePlayerIsIn(player: Player): Game? {
        return lobbyGames.firstOrNull { it.livingPlayers.contains(player) } ?: activeGames.firstOrNull { it.livingPlayers.contains(player) }
    }

    /**
     * Alias for getGamePlayerIsIn for backward compatibility.
     *
     * @param player The player to search for.
     * @return The [Game] the player is in, or null if they are not in any game.
     */
    fun getGame(player: Player): Game? = getGamePlayerIsIn(player)

    /**
     * Adds a player to a random available game.
     *
     * @param player The player to add to a random game.
     * @return true if the player was successfully added to a game, false otherwise.
     */
    fun addPlayerToRandomGame(player: Player): Boolean {
        val game = findRandomAvailableGame() ?: return false
        addPlayerToGame(player, game)
        return true
    }

    /**
     * Adds a player to a game by map name.
     *
     * @param player The player to add.
     * @param mapName The name of the map to join.
     * @return true if the player was successfully added to the game, false otherwise.
     */
    fun addPlayerToGame(player: Player, mapName: String): Boolean {
        val game = getGameByName(mapName) ?: return false
        addPlayerToGame(player, game)
        return true
    }
}