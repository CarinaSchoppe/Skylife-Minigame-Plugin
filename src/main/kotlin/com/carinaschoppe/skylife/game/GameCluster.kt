package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.hub.HubManager
import com.carinaschoppe.skylife.skills.SkillEffectsManager
import com.carinaschoppe.skylife.skills.SkillsManager
import com.carinaschoppe.skylife.utility.scoreboard.ScoreboardManager
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
import com.carinaschoppe.skylife.utility.ui.SkillsGui
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
     * Get a read-only list of all lobby games.
     * Ensures at least one lobby game exists when possible.
     */
    val lobbyGamesList: List<Game>
        get() {
            ensureLobbyGameExists()
            return lobbyGames.toList()
        }

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

        // Load dedicated world for this game using the pattern's map name
        if (!game.loadGameWorld(pattern.mapName)) {
            org.bukkit.Bukkit.getLogger().warning("Failed to load world for game ${game.name} (${game.gameID}) - Map '${pattern.mapName}' not found in maps folder")
        }

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
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4)
        game.livingPlayers.add(player)

        // Teleport to lobby in the game's dedicated world
        val lobbyInGameWorld = com.carinaschoppe.skylife.game.managers.MapManager.locationWorldConverter(
            game.lobbyLocation,
            game
        )
        player.teleport(lobbyInGameWorld)

        game.currentState.playerJoined(player)
        ScoreboardManager.setScoreboard(player, game)
        game.getAllPlayers().forEach { ScoreboardManager.updateScoreboard(it, game) }
    }

    /**
     * Removes a player from the game they are currently in and teleports them back to the hub.
     *
     * @param player The player to remove.
     */
    fun removePlayerFromGame(player: Player) {
        val game = getGamePlayerIsIn(player) ?: return

        game.livingPlayers.remove(player)
        game.spectators.remove(player)
        game.currentState.playerLeft(player)

        // Update scoreboard for remaining players in game
        game.getAllPlayers().forEach { ScoreboardManager.updateScoreboard(it, game) }

        // Reset player for hub
        ScoreboardManager.removeScoreboard(player)
        KitManager.removePlayer(player)
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4)
        if (player.hasPermission("skylife.overview")) {
            player.inventory.setItem(0, GameOverviewItems.createMenuItem())
        }
        // Give skills item
        player.inventory.setItem(4, SkillsGui.createSkillsMenuItem())

        // Teleport to hub
        HubManager.teleportToHub(player)

        // Restore lobby scoreboard AFTER hub teleport
        com.carinaschoppe.skylife.utility.scoreboard.LobbyScoreboardManager.setLobbyScoreboard(player)

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
        // Stop lobby state
        game.currentState.stop()

        // Transition to ingame state
        game.state = GameState.States.INGAME
        game.currentState = com.carinaschoppe.skylife.game.gamestates.IngameState(game)

        lobbyGames.remove(game)
        activeGames.add(game)

        // Increment games counter for all players
        game.livingPlayers.forEach { player ->
            com.carinaschoppe.skylife.utility.statistics.StatsUtility.addStatsToPlayerWhenJoiningGame(player)
        }

        // Start ingame state
        game.currentState.start()
    }

    /**
     * Stops a game, removes it from active games, and creates a fresh game instance.
     * Teleports all players back to the hub and resets their state.
     *
     * @param game The game to stop.
     */
    fun stopGame(game: Game) {
        game.getAllPlayers().forEach { player ->
            // Deactivate skills
            SkillsManager.deactivateSkills(player)
            SkillEffectsManager.removeSkillEffects(player)

            // Reset player for hub
            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)
            if (player.hasPermission("skylife.overview")) {
                player.inventory.setItem(0, GameOverviewItems.createMenuItem())
            }
            // Give skills item
            player.inventory.setItem(4, SkillsGui.createSkillsMenuItem())

            // Teleport to hub
            HubManager.teleportToHub(player)

            // Switch to lobby scoreboard AFTER teleporting to hub
            ScoreboardManager.removeScoreboard(player)
            com.carinaschoppe.skylife.utility.scoreboard.LobbyScoreboardManager.setLobbyScoreboard(player)
        }
        game.livingPlayers.clear()
        game.spectators.clear()

        // Remove the game completely (world will be cleaned up by EndState)
        activeGames.remove(game)

        // Create a fresh game instance for the next round with a new world
        createGameFromPattern(game.pattern)
    }

    /**
     * Finds a random, available game for a player to join.
     *
     * @return An available [Game], or null if none are found.
     */
    fun findRandomAvailableGame(): Game? {
        val availableGames = ensureLobbyGameAvailable()
        return if (availableGames.isEmpty()) null else availableGames.random()
    }

    private fun ensureLobbyGameExists(): Boolean {
        if (lobbyGames.isNotEmpty()) {
            return true
        }
        val pattern = randomCompletePattern() ?: return false
        createGameFromPattern(pattern)
        return true
    }

    private fun ensureLobbyGameAvailable(): List<Game> {
        val availableGames = lobbyGames.filter { it.livingPlayers.size < it.maxPlayers }
        if (availableGames.isNotEmpty()) {
            return availableGames
        }
        val pattern = randomCompletePattern() ?: return emptyList()
        val game = createGameFromPattern(pattern)
        return listOf(game)
    }

    private fun randomCompletePattern(): GamePattern? {
        val patterns = gamePatterns.filter { it.isComplete() }
        return if (patterns.isEmpty()) null else patterns.random()
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
     * Only allows joining games in the lobby state.
     *
     * @param player The player to add.
     * @param mapName The name of the map to join.
     * @return true if the player was successfully added to the game, false otherwise.
     */
    fun addPlayerToGame(player: Player, mapName: String): Boolean {
        val game = getGameByName(mapName) ?: return false
        if (game.state != GameState.States.LOBBY) return false
        
        addPlayerToGame(player, game)
        return true
    }
}
