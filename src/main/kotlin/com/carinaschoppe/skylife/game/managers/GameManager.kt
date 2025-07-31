package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit

object GameManager {


    fun checkGameOver(game: Game): Boolean {
        if (game.currentState !is IngameState) return false

        if (game.livingPlayers.size > 2) {
            return false
        }

        Bukkit.getServer().consoleSender.sendMessage("Game Over: '${game.gameID}'")
        //TODO: fehlt da was?
        game.currentState.stop()
        return true
    }


    fun endingMatchMessage(game: Game) {
        game.livingPlayers.forEach {
            if (game.livingPlayers.size == 1)
                it.sendMessage(Messages.PLAYER_WON(game.livingPlayers[0].name))
            it.sendMessage(Messages.GAME_OVER())
        }
        game.spectators.forEach {
            if (game.livingPlayers.size == 1)
                it.sendMessage(Messages.PLAYER_WON(game.livingPlayers[0].name))
            it.sendMessage(Messages.GAME_OVER())
        }


    }

}