package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.services.*
import org.bukkit.entity.Player

/**
 * Facade for game cluster operations. Delegates to [GameClusterService]
 * to keep state management and player side-effects separated.
 */
object GameCluster {

    @Volatile
    private var service: GameClusterService = GameClusterService(
        InMemoryGameRegistry(),
        DefaultGameFactory(),
        DefaultPlayerSessionService(),
        DefaultGameConfigProvider(),
        DefaultPlayerPriorityResolver(),
        DefaultGameMessageProvider(),
        DefaultStatsService(),
        DefaultSkillLifecycleService(),
        DefaultGameStateFactory()
    )

    fun initialize(customService: GameClusterService) {
        service = customService
    }

    val activeGamesList: List<Game>
        get() = service.activeGamesList

    val lobbyGamesList: List<Game>
        get() = service.lobbyGamesList

    val gamePatterns: List<GamePattern>
        get() = service.gamePatterns

    fun addGamePattern(pattern: GamePattern) {
        service.addGamePattern(pattern)
    }

    fun removeGamePattern(pattern: GamePattern) {
        service.removeGamePattern(pattern)
    }

    fun createGameFromPattern(pattern: GamePattern): Game = service.createGameFromPattern(pattern)

    fun addGame(game: Game) {
        service.addGame(game)
    }

    fun addPlayerToGame(player: Player, game: Game): Boolean = service.addPlayerToGame(player, game)

    fun removePlayerFromGame(player: Player) {
        service.removePlayerFromGame(player)
    }

    fun startGame(game: Game) {
        service.startGame(game)
    }

    fun stopGame(game: Game) {
        service.stopGame(game)
    }

    fun findRandomAvailableGame(): Game? = service.findRandomAvailableGame()

    fun getGameByName(name: String): Game? = service.getGameByName(name)

    fun getGamePlayerIsIn(player: Player): Game? = service.getGamePlayerIsIn(player)

    fun getGame(player: Player): Game? = service.getGame(player)

    fun addPlayerToRandomGame(player: Player): Boolean = service.addPlayerToRandomGame(player)

    fun addPlayerToGame(player: Player, mapName: String): Boolean = service.addPlayerToGame(player, mapName)
}
