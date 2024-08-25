package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.management.GameCluster
import com.carinaschoppe.skylife.game.management.gamestates.EndState
import com.carinaschoppe.skylife.game.management.gamestates.IngameState
import com.carinaschoppe.skylife.game.management.gamestates.LobbyState
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerChatsEvent : Listener {


    @EventHandler(ignoreCancelled = true)
    fun onAsyncChat(event: AsyncChatEvent) {
        event.isCancelled = true
        //check if player is in a game
        val message: Component

        val game = GameCluster.lobbyGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: GameCluster.activeGames.firstOrNull { it.livingPlayers.contains(event.player) or it.spectators.contains(event.player) } ?: run {
            //player is in lobby.

            val players = Bukkit.getServer().onlinePlayers.toMutableList()
            GameCluster.lobbyGames.forEach { game -> players.removeAll(game.livingPlayers); players.removeAll(game.spectators) }
            GameCluster.activeGames.forEach { game -> players.removeAll(game.livingPlayers); players.removeAll(game.spectators) }
            message = Component.text("[HUB] ", NamedTextColor.GRAY).append(Component.text("${event.player.name}: ", NamedTextColor.WHITE).append(event.message()))

            players.forEach { it.sendMessage(message) }

            return
        }

        if (game.currentState is LobbyState) {
            message = Component.text("[LOBBY] ", NamedTextColor.GRAY).append(Component.text("${event.player.name}: ", NamedTextColor.WHITE).append(event.message()))
            game.livingPlayers.forEach { it.sendMessage(message) }
            game.spectators.forEach { it.sendMessage(message) }
        } else if (game.currentState is IngameState) {
            if (game.livingPlayers.contains(event.player)) {
                message = Component.text("[INGAME] ", NamedTextColor.GRAY).append(Component.text("${event.player.name}: ", NamedTextColor.WHITE).append(event.message()))
                game.livingPlayers.forEach { it.sendMessage(message) }
                game.spectators.forEach { it.sendMessage(message) }
            } else {
                message = Component.text("[SPECTATOR] ", NamedTextColor.GRAY).append(Component.text("${event.player.name}: ", NamedTextColor.WHITE).append(event.message()))
                game.spectators.forEach { it.sendMessage(message) }
            }
        } else if (game.currentState is EndState) {
            message = Component.text("[END] ", NamedTextColor.GRAY).append(Component.text("${event.player.name}: ", NamedTextColor.WHITE).append(event.message()))
            game.livingPlayers.forEach { it.sendMessage(message) }
            game.spectators.forEach { it.sendMessage(message) }
        }
    }
}