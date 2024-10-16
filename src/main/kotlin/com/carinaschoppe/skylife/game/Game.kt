package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.gamestates.EndState
import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.game.gamestates.LobbyState
import com.carinaschoppe.skylife.game.managers.MapManager
import org.bukkit.block.Chest
import org.bukkit.entity.Player

class Game {

    lateinit var gamePattern: GamePattern

    var gameID: Int = 0

    init {
        gameID = GameCluster.gameID++
    }

    val gameStats = listOf(LobbyState(this), IngameState(this), EndState(this))

    lateinit var currentState: GameState

    val gameChests = mutableListOf<Chest>()

    val livingPlayers = mutableListOf<Player>()

    val spectators = mutableListOf<Player>()


    fun gameStateInitialized(): Boolean {
        return ::currentState.isInitialized
    }

    fun cancel() {
        livingPlayers.toList().forEach { GameCluster.removePlayerFromGame(it) }
        spectators.toList().forEach { GameCluster.removePlayerFromGame(it) }
        GameCluster.activeGames.remove(this)
        MapManager.unloadWorld(this)
    }
}