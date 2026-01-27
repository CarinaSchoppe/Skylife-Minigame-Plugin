package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.guild.GuildManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Listener to update player display names with guild tags.
 */
class PlayerDisplayNameListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        updatePlayerDisplayName(event.player)
    }

    companion object {
        /**
         * Updates a player's display name to include their guild tag if they're in a guild.
         */
        fun updatePlayerDisplayName(player: org.bukkit.entity.Player) {
            val tag = GuildManager.getFormattedTag(player.uniqueId)
            if (tag != null) {
                player.displayName(Component.text(tag, Messages.ACCENT_COLOR).append(Component.text(" ${player.name}", NamedTextColor.WHITE)))
            } else {
                player.displayName(Component.text(player.name, NamedTextColor.WHITE))
            }
        }
    }
}
