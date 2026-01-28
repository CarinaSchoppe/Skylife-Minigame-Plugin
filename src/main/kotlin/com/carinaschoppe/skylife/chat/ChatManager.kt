package com.carinaschoppe.skylife.chat

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.guild.GuildManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Manages all chat routing and message delivery based on player context and message type.
 *
 * Chat Rules:
 * - Default: Round-based chat (players only see messages from their game)
 * - @all <message>: Global chat visible to all players
 * - @guild <message>: Guild-only chat
 * - /msg <player> <message>: Direct messages (handled by MessageCommand)
 *
 * Player States and Visibility:
 * - Alive players: See their round chat, global @all messages, guild messages, and DMs
 * - Dead players (spectators): See spectator chat, other spectators' messages, DMs, but NOT alive players' messages
 * - Alive players cannot send DMs to dead players in the same game
 */
object ChatManager {

    /**
     * Processes a chat message and routes it to the appropriate recipients.
     * @return true if message was handled, false if it should be cancelled
     */
    fun processMessage(sender: Player, message: String): Boolean {
        val plainMessage = stripFormatting(message)

        // Check for special prefixes
        return when {
            plainMessage.startsWith("@all ", ignoreCase = true) -> {
                handleGlobalChat(sender, plainMessage.substring(5))
                true
            }

            plainMessage.startsWith("@guild ", ignoreCase = true) -> {
                handleGuildChat(sender, plainMessage.substring(7))
                true
            }

            else -> {
                handleRoundChat(sender, plainMessage)
                true
            }
        }
    }

    /**
     * Handles global chat (@all messages).
     * All online players can see these messages.
     */
    private fun handleGlobalChat(sender: Player, message: String) {
        val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(sender)
        val rankTag = if (rank != com.carinaschoppe.skylife.economy.PlayerRank.USER && rank.tag.isNotEmpty()) rank.tag else ""
        val rankColor = getRankColor(rank)
        val guildTag = GuildManager.getFormattedTag(sender.uniqueId) ?: ""

        val formattedMessage = Component.text("[GLOBAL] ", NamedTextColor.GOLD)
            .append(if (rankTag.isNotEmpty()) Component.text(rankTag, rankColor) else Component.empty())
            .append(if (guildTag.isNotEmpty()) Component.text("$guildTag ", Messages.ACCENT_COLOR) else Component.empty())
            .append(Component.text(sender.name, NamedTextColor.WHITE))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(Component.text(message, NamedTextColor.WHITE))

        Bukkit.getOnlinePlayers().forEach { it.sendMessage(formattedMessage) }
    }

    /**
     * Handles guild chat (@guild messages).
     * Only guild members can see these messages.
     */
    private fun handleGuildChat(sender: Player, message: String) {
        val guildId = GuildManager.getPlayerGuildId(sender.uniqueId)
        if (guildId == null) {
            sender.sendMessage(Messages.PREFIX.append(Component.text("You are not in a guild", Messages.ERROR_COLOR)))
            return
        }

        val guild = GuildManager.getGuild(guildId)
        if (guild == null) {
            sender.sendMessage(Messages.PREFIX.append(Component.text("Guild not found", Messages.ERROR_COLOR)))
            return
        }
        val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(sender)
        val rankTag = if (rank != com.carinaschoppe.skylife.economy.PlayerRank.USER && rank.tag.isNotEmpty()) rank.tag else ""
        val rankColor = getRankColor(rank)
        val guildTag = "[${guild.tag}]"

        val formattedMessage = Component.text("[GUILD] ", NamedTextColor.GREEN)
            .append(if (rankTag.isNotEmpty()) Component.text(rankTag, rankColor) else Component.empty())
            .append(Component.text("$guildTag ", Messages.ACCENT_COLOR))
            .append(Component.text(sender.name, NamedTextColor.WHITE))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(Component.text(message, NamedTextColor.WHITE))

        // Send to all online guild members
        Bukkit.getOnlinePlayers()
            .filter { GuildManager.getPlayerGuildId(it.uniqueId) == guildId }
            .forEach { it.sendMessage(formattedMessage) }
    }

    /**
     * Handles round-based chat (default chat).
     * Message visibility depends on player state:
     * - Alive players: Only other alive players in the same game see the message
     * - Dead/Spectators: Only other dead/spectators in the same game see the message
     */
    private fun handleRoundChat(sender: Player, message: String) {
        val game = GameCluster.getGame(sender)

        if (game == null) {
            // Player is in hub
            handleHubChat(sender, message)
            return
        }

        val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(sender)
        val rankTag = if (rank != com.carinaschoppe.skylife.economy.PlayerRank.USER && rank.tag.isNotEmpty()) rank.tag else ""
        val rankColor = getRankColor(rank)
        val isSpectator = game.spectators.contains(sender)
        val guildTag = GuildManager.getFormattedTag(sender.uniqueId) ?: ""

        if (isSpectator) {
            // Spectator chat - only visible to other spectators in the same game
            val formattedMessage = Component.text("[SPECTATOR] ", NamedTextColor.AQUA)
                .append(if (rankTag.isNotEmpty()) Component.text(rankTag, rankColor) else Component.empty())
                .append(if (guildTag.isNotEmpty()) Component.text("$guildTag ", Messages.ACCENT_COLOR) else Component.empty())
                .append(Component.text(sender.name, NamedTextColor.WHITE))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.WHITE))

            game.spectators.forEach { it.sendMessage(formattedMessage) }
        } else {
            // Alive player chat - visible to all alive players in the same game
            val prefix = if (game.currentState is IngameState) "[INGAME]" else "[LOBBY]"
            val prefixColor = if (game.currentState is IngameState) NamedTextColor.YELLOW else NamedTextColor.GREEN

            val formattedMessage = Component.text("$prefix ", prefixColor)
                .append(if (rankTag.isNotEmpty()) Component.text(rankTag, rankColor) else Component.empty())
                .append(if (guildTag.isNotEmpty()) Component.text("$guildTag ", Messages.ACCENT_COLOR) else Component.empty())
                .append(Component.text(sender.name, NamedTextColor.WHITE))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.WHITE))

            game.livingPlayers.forEach { it.sendMessage(formattedMessage) }
        }
    }

    /**
     * Handles hub chat for players not in any game.
     */
    private fun handleHubChat(sender: Player, message: String) {
        val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(sender)
        val rankTag = if (rank != com.carinaschoppe.skylife.economy.PlayerRank.USER && rank.tag.isNotEmpty()) rank.tag else ""
        val rankColor = getRankColor(rank)
        val guildTag = GuildManager.getFormattedTag(sender.uniqueId) ?: ""

        val formattedMessage = Component.text("[HUB] ", NamedTextColor.GRAY)
            .append(if (rankTag.isNotEmpty()) Component.text(rankTag, rankColor) else Component.empty())
            .append(if (guildTag.isNotEmpty()) Component.text("$guildTag ", Messages.ACCENT_COLOR) else Component.empty())
            .append(Component.text(sender.name, NamedTextColor.WHITE))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(Component.text(message, NamedTextColor.WHITE))

        // Send to all players in hub (not in any game)
        Bukkit.getOnlinePlayers()
            .filter { GameCluster.getGame(it) == null }
            .forEach { it.sendMessage(formattedMessage) }
    }

    /**
     * Checks if a player can send a direct message to another player.
     * Rules:
     * - Can always DM players in different games or in hub
     * - Cannot DM alive players if you're dead in the same game
     * - Dead players can DM each other in the same game
     */
    fun canSendDirectMessage(sender: Player, recipient: Player): Boolean {
        val senderGame = GameCluster.getGame(sender)
        val recipientGame = GameCluster.getGame(recipient)

        // Different games or hub - always allowed
        if (senderGame != recipientGame) {
            return true
        }

        // Same game - check if both are alive or both are dead
        if (senderGame != null) {
            val senderDead = senderGame.spectators.contains(sender)
            val recipientDead = senderGame.spectators.contains(recipient)

            // Alive can't DM dead in same game, and vice versa
            if (senderDead != recipientDead) {
                return false
            }
        }

        return true
    }

    /**
     * Strips MiniMessage formatting codes from a message to get plain text.
     */
    private fun stripFormatting(message: String): String {
        // For now, just return the message as-is since we're dealing with plain strings
        // If using Components, this would need to extract plain text
        return message
    }

    /**
     * Gets the display color for a player rank.
     */
    private fun getRankColor(rank: com.carinaschoppe.skylife.economy.PlayerRank): NamedTextColor {
        return rank.getColor()
    }
}
