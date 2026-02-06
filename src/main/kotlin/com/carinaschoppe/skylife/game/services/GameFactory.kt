package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.game.gamestates.LobbyState
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.game.managers.MapManager
import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*

interface GameFactory {
    fun createFromPattern(pattern: GamePattern): Game
}

interface GameLocationConverter {
    fun toLocation(skylifeLocation: SkylifeLocation): Location?
}

class DefaultGameLocationConverter : GameLocationConverter {
    override fun toLocation(skylifeLocation: SkylifeLocation): Location? {
        return GameLocationManager.skylifeLocationToLocationConverter(skylifeLocation)
    }
}

interface GameWorldLoader {
    fun loadWorldForGame(gameId: UUID, templateMapName: String? = null): World?
}

class DefaultGameWorldLoader : GameWorldLoader {
    override fun loadWorldForGame(gameId: UUID, templateMapName: String?): World? {
        return MapManager.loadMapForGame(gameId, templateMapName)
    }
}

class DefaultGameFactory(
    private val locationConverter: GameLocationConverter = DefaultGameLocationConverter(),
    private val worldLoader: GameWorldLoader = DefaultGameWorldLoader()
) : GameFactory {
    override fun createFromPattern(pattern: GamePattern): Game {
        val lobbyLoc = locationConverter.toLocation(pattern.gameLocationManager.lobbyLocation)
        val ingameLoc = locationConverter.toLocation(pattern.gameLocationManager.mainLocation)

        if (lobbyLoc == null || ingameLoc == null) {
            throw IllegalStateException("Failed to create game '${pattern.mapName}': Required worlds not loaded")
        }

        val game = Game(
            name = pattern.mapName,
            minPlayers = pattern.minPlayers,
            maxPlayers = pattern.maxPlayers,
            lobbyLocation = lobbyLoc,
            ingameLocation = ingameLoc,
            mapName = pattern.mapName,
            pattern = pattern,
            stateFactory = { created -> LobbyState(created) },
            messageSender = { player, message -> player.sendMessage(message) }
        )

        // Load dedicated world for this game using the pattern's map name
        val world = worldLoader.loadWorldForGame(game.gameID, pattern.mapName)
        game.attachGameWorld(world)
        if (world == null) {
            Bukkit.getLogger().warning(
                "Failed to load world for game ${game.name} (${game.gameID}) - Map '${pattern.mapName}' not found in maps folder"
            )
        }

        return game
    }
}
