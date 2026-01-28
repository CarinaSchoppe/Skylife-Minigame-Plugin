package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.guild.GuildManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Listener to update player display names with rank and guild tags.
 */
class PlayerDisplayNameListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        updatePlayerDisplayName(event.player)
    }

    companion object {
        /**
         * Updates a player's display name, tab list name, and custom name (nameplate above head).
         * Format: [RANK] [GUILD] PlayerName
         */
        fun updatePlayerDisplayName(player: org.bukkit.entity.Player) {
            val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(player)
            val guildTag = GuildManager.getFormattedTag(player.uniqueId)

            // Build display name with rank tag
            var displayName = Component.empty()

            // Add rank tag if not USER
            if (rank != com.carinaschoppe.skylife.economy.PlayerRank.USER && rank.tag.isNotEmpty()) {
                displayName = displayName.append(Component.text(rank.tag, getRankColor(rank)))
            }

            // Add guild tag if in guild
            if (guildTag != null) {
                displayName = displayName.append(Component.text(guildTag, Messages.ACCENT_COLOR).append(Component.text(" ")))
            }

            // Add player name
            displayName = displayName.append(Component.text(player.name, NamedTextColor.WHITE))

            // Build custom name (above head) - rank on top, name below
            var customName = Component.empty()

            // Add rank tag if not USER
            if (rank != com.carinaschoppe.skylife.economy.PlayerRank.USER && rank.tag.isNotEmpty()) {
                customName = Component.text(rank.tag.trim(), getRankColor(rank))
                    .append(Component.newline())
            }

            // Add player name
            customName = customName.append(Component.text(player.name, NamedTextColor.WHITE))

            // Set display name (for chat), player list name (for tab list), and custom name (above head)
            player.displayName(displayName)
            player.playerListName(displayName)
            player.customName(customName)
            player.isCustomNameVisible = true
        }

        private fun getRankColor(rank: com.carinaschoppe.skylife.economy.PlayerRank): NamedTextColor {
            return when (rank) {
                com.carinaschoppe.skylife.economy.PlayerRank.VIP -> NamedTextColor.GREEN
                com.carinaschoppe.skylife.economy.PlayerRank.VIP_PLUS -> NamedTextColor.GOLD
                else -> NamedTextColor.GRAY
            }
        }
    }
}
