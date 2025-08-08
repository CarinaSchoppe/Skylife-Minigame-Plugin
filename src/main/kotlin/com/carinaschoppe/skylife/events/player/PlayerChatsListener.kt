package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.EndState
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.game.gamestates.LobbyState
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Listener to manage the server's chat system.
 *
 * This listener takes full control of the chat by cancelling the `AsyncChatEvent` globally.
 * It then formats and redirects messages based on the player's context:
 * - **Hub:** Players not in any game can talk to each other.
 * - **Game-specific:** Players within a game talk to others in the same game.
 *   The chat prefix changes based on the game state (Lobby, Ingame, Spectator, End).
 */
class PlayerChatsListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onAsyncChat(event: AsyncChatEvent) {
        // This listener takes over all chat handling.
        event.isCancelled = true

        val player = event.player
        val game = GameCluster.getGame(player)

        if (game == null) {
            // Player is not in a game, handle as Hub chat.
            handleHubChat(player, event.message())
        } else {
            // Player is in a game, handle based on game state.
            handleGameChat(player, game, event.message())
        }
    }

    /**
     * Handles chat for players who are not inside a game.
     */
    private fun handleHubChat(sender: Player, message: Component) {
        val formattedMessage = createFormattedMessage("HUB", NamedTextColor.GRAY, sender, message)

        // Send the message to all other players who are also in the hub.
        Bukkit.getOnlinePlayers().forEach { recipient ->
            if (GameCluster.getGame(recipient) == null) {
                recipient.sendMessage(formattedMessage)
            }
        }
    }

    /**
     * Handles chat for players who are inside a game, routing it based on the game's state.
     */
    private fun handleGameChat(sender: Player, game: Game, message: Component) {
        val formattedMessage = when (game.currentState) {
            is LobbyState -> createFormattedMessage("LOBBY", NamedTextColor.GREEN, sender, message)
            is IngameState -> {
                if (game.spectators.contains(sender)) {
                    createFormattedMessage("SPECTATOR", NamedTextColor.AQUA, sender, message)
                } else {
                    createFormattedMessage("INGAME", NamedTextColor.YELLOW, sender, message)
                }
            }

            is EndState -> createFormattedMessage("END", NamedTextColor.RED, sender, message)
            else -> createFormattedMessage("GAME", NamedTextColor.WHITE, sender, message) // Fallback
        }

        // Broadcast the message to all players and spectators in the same game.
        game.broadcast(formattedMessage)
    }

    /**
     * Helper function to create a consistently formatted chat message.
     */
    private fun createFormattedMessage(prefix: String, prefixColor: TextColor, sender: Player, message: Component): Component {
        return Component.text("[$prefix] ", prefixColor)
            .append(sender.displayName())
            .append(Component.text(": ", NamedTextColor.WHITE))
            .append(message)
    }
}