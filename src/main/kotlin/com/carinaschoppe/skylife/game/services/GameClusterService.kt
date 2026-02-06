package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.game.gamestates.GameStateType
import com.carinaschoppe.skylife.utility.PlayerPriority
import org.bukkit.entity.Player

class GameClusterService(
    private val registry: GameRegistry,
    private val gameFactory: GameFactory,
    private val playerSessionService: PlayerSessionService,
    private val configProvider: GameConfigProvider,
    private val playerPriorityResolver: PlayerPriorityResolver,
    private val messageProvider: GameMessageProvider,
    private val statsService: StatsService,
    private val skillLifecycleService: SkillLifecycleService,
    private val gameStateFactory: GameStateFactory
) {

    val activeGamesList: List<Game>
        get() = registry.activeGames.toList()

    val lobbyGamesList: List<Game>
        get() {
            ensureLobbyGameExists()
            return registry.lobbyGames.toList()
        }

    val gamePatterns: List<GamePattern>
        get() = registry.gamePatterns.toList()

    fun addGamePattern(pattern: GamePattern) {
        if (!registry.gamePatterns.contains(pattern)) {
            registry.gamePatterns.add(pattern)
        }
    }

    fun removeGamePattern(pattern: GamePattern) {
        registry.gamePatterns.remove(pattern)
    }

    fun createGameFromPattern(pattern: GamePattern): Game {
        val game = gameFactory.createFromPattern(pattern)
        registry.lobbyGames.add(game)
        return game
    }

    fun addGame(game: Game) {
        registry.lobbyGames.add(game)
    }

    @Synchronized
    fun addPlayerToGame(player: Player, game: Game): Boolean {
        if (game.state != GameStateType.LOBBY) {
            player.sendMessage(messageProvider.gameFullOrStarted())
            return false
        }

        if (game.livingPlayers.size >= game.maxPlayers) {
            val config = configProvider.priorityJoinConfig()
            val joiningPriority = playerPriorityResolver.getPlayerPriority(player)

            val priorityEnabled = when (joiningPriority) {
                PlayerPriority.VIP -> config.enabledForVip
                PlayerPriority.VIP_PLUS -> config.enabledForVipPlus
                PlayerPriority.STAFF -> config.enabledForStaff
                else -> false
            }

            if (!priorityEnabled) {
                player.sendMessage(messageProvider.gameFullOrStarted())
                return false
            }

            val playerToKick = playerPriorityResolver.findPlayerToKick(
                game.livingPlayers.toList(),
                player
            )

            if (playerToKick == null) {
                player.sendMessage(messageProvider.priorityJoinFull())
                return false
            }

            playerToKick.sendMessage(messageProvider.priorityJoinKicked(player.name))
            removePlayerFromGame(playerToKick)
        }

        game.livingPlayers.add(player)

        playerSessionService.prepareForGameJoin(player, game)
        game.currentState.playerJoined(player)
        playerSessionService.attachGameScoreboard(player, game)
        playerSessionService.updateGameScoreboards(game)

        return true
    }

    fun removePlayerFromGame(player: Player) {
        val game = getGamePlayerIsIn(player) ?: return

        game.livingPlayers.remove(player)
        game.spectators.remove(player)
        game.currentState.playerLeft(player)

        playerSessionService.updateGameScoreboards(game)
        playerSessionService.resetToHubFromGame(player)

        if (game.state == GameStateType.INGAME && game.livingPlayers.size <= 1) {
            game.stop()
        }
    }

    fun startGame(game: Game) {
        game.currentState.stop()
        game.state = GameStateType.INGAME
        game.currentState = gameStateFactory.createIngameState(game)

        registry.lobbyGames.remove(game)
        registry.activeGames.add(game)

        game.livingPlayers.forEach { player ->
            statsService.addStatsToPlayerWhenJoiningGame(player)
        }

        game.currentState.start()
    }

    fun stopGame(game: Game) {
        game.getAllPlayers().forEach { player ->
            skillLifecycleService.deactivateSkills(player)
            skillLifecycleService.removeSkillEffects(player)
            playerSessionService.resetToHubAfterGameStop(player)
        }
        game.livingPlayers.clear()
        game.spectators.clear()

        registry.activeGames.remove(game)
        createGameFromPattern(game.pattern)
    }

    fun findRandomAvailableGame(): Game? {
        val availableGames = ensureLobbyGameAvailable()
        return if (availableGames.isEmpty()) null else availableGames.random()
    }

    fun getGameByName(name: String): Game? {
        return registry.lobbyGames.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: registry.activeGames.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun getGamePlayerIsIn(player: Player): Game? {
        return registry.lobbyGames.firstOrNull { it.livingPlayers.contains(player) }
            ?: registry.activeGames.firstOrNull { it.livingPlayers.contains(player) }
    }

    fun getGame(player: Player): Game? = getGamePlayerIsIn(player)

    fun addPlayerToRandomGame(player: Player): Boolean {
        val game = findRandomAvailableGame() ?: return false
        return addPlayerToGame(player, game)
    }

    fun addPlayerToGame(player: Player, mapName: String): Boolean {
        val game = getGameByName(mapName) ?: return false
        return addPlayerToGame(player, game)
    }

    private fun ensureLobbyGameExists(): Boolean {
        if (registry.lobbyGames.isNotEmpty()) {
            return true
        }
        val pattern = randomCompletePattern() ?: return false
        createGameFromPattern(pattern)
        return true
    }

    private fun ensureLobbyGameAvailable(): List<Game> {
        val availableGames = registry.lobbyGames.filter { it.livingPlayers.size < it.maxPlayers }
        if (availableGames.isNotEmpty()) {
            return availableGames
        }
        val pattern = randomCompletePattern() ?: return emptyList()
        val game = createGameFromPattern(pattern)
        return listOf(game)
    }

    private fun randomCompletePattern(): GamePattern? {
        val patterns = registry.gamePatterns.filter { it.isComplete() }
        return if (patterns.isEmpty()) null else patterns.random()
    }
}
