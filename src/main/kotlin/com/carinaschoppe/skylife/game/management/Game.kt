package com.carinaschoppe.skylife.game.management

import com.carinaschoppe.skylife.game.management.gamestates.EndState
import com.carinaschoppe.skylife.game.management.gamestates.GameState
import com.carinaschoppe.skylife.game.management.gamestates.IngameState
import com.carinaschoppe.skylife.game.management.gamestates.LobbyState
import com.carinaschoppe.skylife.game.miscellaneous.Utility
import org.bukkit.entity.Player

class Game {

    lateinit var gamePattern: GamePattern

    var gameID: Int = 0

    init {
        gameID = GameCluster.gameID++
    }

    val gameStats = listOf(LobbyState(this), IngameState(this), EndState(this))

    lateinit var currentState: GameState

    val livingPlayers = mutableListOf<Player>()

    val spectators = mutableListOf<Player>()


    fun gameStateInitialized(): Boolean {
        return ::currentState.isInitialized
    }

    fun cancel() {


        livingPlayers.forEach { GameCluster.removePlayerFromGame(it) }
        spectators.forEach { GameCluster.removePlayerFromGame(it) }
        GameCluster.activeGames.remove(this)
        Utility.unloadWorld(this)
    }
}