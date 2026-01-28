package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.gamestates.EndState
import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

/**
 * Manages game-wide logic and state transitions.
 */
object GameManager {

    /**
     * Checks if the game should end based on the number of living players.
     * If the game should end, it transitions to the end state.
     *
     * @param game The game to check
     * @return true if the game is ending, false otherwise
     */
    fun checkGameOver(game: Game): Boolean {
        // Only check for game over if we're in the ingame state
        if (game.currentState !is IngameState) return false

        // Game continues if there are more than 1 player
        if (game.livingPlayers.size > 1) {
            return false
        }

        Bukkit.getServer().consoleSender.sendMessage(
            Component.text("Game Over: '${game.gameID}' - ${game.livingPlayers.size} players remaining")
        )

        // Transition to end state
        game.currentState.stop()
        game.state = GameState.States.END
        game.currentState = EndState(game)
        game.currentState.start()
        
        return true
    }

    /**
     * Sends the end-of-match messages to all players and spectators.
     *
     * @param game The game that has ended
     */
    fun endingMatchMessage(game: Game) {
        // Send messages to living players
        game.livingPlayers.forEach { player ->
            if (game.livingPlayers.size == 1) {
                player.sendMessage(Messages.PLAYER_WON(player.name))
            }
            player.sendMessage(Messages.GAME_OVER)
        }

        // Send messages to spectators
        game.spectators.forEach { spectator ->
            if (game.livingPlayers.size == 1) {
                spectator.sendMessage(Messages.PLAYER_WON(game.livingPlayers[0].name))
            }
            spectator.sendMessage(Messages.GAME_OVER)
        }
    }
}
