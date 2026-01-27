package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.chat.ChatManager
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Listener to manage the server's chat system.
 *
 * This listener takes full control of the chat by cancelling the `AsyncChatEvent` globally.
 * It delegates all chat processing to ChatManager which handles:
 * - Round-based chat (default)
 * - Global chat (@all prefix)
 * - Guild chat (@guild prefix)
 * - Spectator chat
 * - Hub chat
 */
class PlayerChatsListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onAsyncChat(event: AsyncChatEvent) {
        // Cancel the default chat event
        event.isCancelled = true

        val player = event.player
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())

        // Process the message through ChatManager
        ChatManager.processMessage(player, message)
    }
}