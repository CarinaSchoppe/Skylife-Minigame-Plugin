package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.gamestates.EndState
import com.carinaschoppe.skylife.game.gamestates.GameStates
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.game.managers.GameManager
import com.carinaschoppe.skylife.game.managers.MapManager
import com.carinaschoppe.skylife.utility.messages.Messages

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

object GameCluster {

    var gameID = 0

    val gamePatterns = mutableSetOf<GamePattern>()

    val activeGames = mutableSetOf<Game>()

    val lobbyGames = mutableListOf<Game>()


    private fun createGame(mapName: String): Game {
        val game = Game()
        game.gamePattern = gamePatterns.first { it.mapName == mapName }
        MapManager.loadGameWorld(game)
        lobbyGames.add(game)
        Bukkit.getServer().consoleSender.sendMessage("Created Game: '${game.gamePattern.mapName}'")
        return game
    }

    fun addPlayerToRandomGame(player: Player) {
        val filteredGames = lobbyGames.filter { it.livingPlayers.size < it.gamePattern.maxPlayers }
        if (filteredGames.isNotEmpty()) {
            val game = filteredGames.minByOrNull { it.livingPlayers.size }!!
            addPlayerToGame(player, game)
        } else {
            val gamePattern = gamePatterns.random()
            val game = createGame(gamePattern.mapName)
            addPlayerToGame(player, game)
        }
    }

    private fun addPlayerToGame(player: Player, game: Game) {
        //TODO: mehr hier?
        player.gameMode = GameMode.SURVIVAL
        player.allowFlight = false
        player.clearActivePotionEffects()
        game.livingPlayers.add(player)
        if (!game.gameStateInitialized() && game.livingPlayers.size >= game.gamePattern.minPlayers) {
            game.currentState = game.gameStats[GameStates.LOBBY_STATE.id]
            game.currentState.start()
        }
        player.sendMessage(Messages.PLAYER_JOINS_GAME(game.gamePattern.mapName))

        Bukkit.getServer().consoleSender.sendMessage(MapManager.locationWorldConverter(GameLocationManager.Companion.skylifeLocationToLocationConverter(game.gamePattern.gameLocationManager.lobbyLocation), game).toString())

        player.teleport(MapManager.locationWorldConverter(GameLocationManager.Companion.skylifeLocationToLocationConverter(game.gamePattern.gameLocationManager.lobbyLocation), game))
        //message player joined

        game.livingPlayers.forEach {
            it.showPlayer(Skylife.instance, player)
            it.sendMessage(Messages.PLAYER_JOINED(player.name, game.livingPlayers.size, game.gamePattern.maxPlayers))
            if (!game.gameStateInitialized() && game.livingPlayers.size < game.gamePattern.minPlayers) {
                //missing players
                it.sendMessage(Messages.PLAYER_MISSING(game.livingPlayers.size, game.gamePattern.minPlayers))
            }
        }

        game.spectators.forEach {
            it.sendMessage(Messages.PLAYER_JOINED(player.name, game.livingPlayers.size, game.gamePattern.maxPlayers))
            it.showPlayer(Skylife.instance, player)
            player.hidePlayer(Skylife.instance, it)
            if (!game.gameStateInitialized() && game.livingPlayers.size < game.gamePattern.minPlayers) {
                //missing players
                it.sendMessage(Messages.PLAYER_MISSING(game.livingPlayers.size, game.gamePattern.minPlayers))
            }
        }


    }

    fun addPlayerToGame(player: Player, mapName: String) {
        val existingGame = lobbyGames.firstOrNull { it.gamePattern.mapName == mapName && it.livingPlayers.size < it.gamePattern.maxPlayers }
        if (existingGame != null) {
            Bukkit.getServer().consoleSender.sendMessage("Adding player to existing game")
            addPlayerToGame(player, existingGame)
        } else {
            Bukkit.getServer().consoleSender.sendMessage("creating new game")

            val game = createGame(mapName)
            addPlayerToGame(player, game)
        }
    }

    fun removePlayerFromGame(player: Player) {
        val game = lobbyGames.firstOrNull { it.livingPlayers.contains(player) } ?: activeGames.firstOrNull { it.livingPlayers.contains(player) } ?: return
        game.spectators.remove(player)
        game.livingPlayers.remove(player)
        player.allowFlight = false
        player.clearActivePotionEffects()
        player.gameMode = GameMode.ADVENTURE
        player.teleport(GameLocationManager.Companion.skylifeLocationToLocationConverter(game.gamePattern.gameLocationManager.mainLocation))
        Bukkit.getOnlinePlayers().forEach {
            it.showPlayer(Skylife.instance, player)
            player.showPlayer(Skylife.instance, it)
        }
        GameManager.checkGameOver(game)


        //TODO: update scoreboard

        if (game.currentState is EndState) {
            return
        }



        game.livingPlayers.forEach {
            it.sendMessage(Messages.PLAYER_LEFT(player.name))
        }

        game.spectators.forEach {
            it.sendMessage(Messages.PLAYER_LEFT(player.name))
        }

    }


}