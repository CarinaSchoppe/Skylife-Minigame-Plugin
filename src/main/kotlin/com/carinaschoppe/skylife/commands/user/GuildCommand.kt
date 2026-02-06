package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.guild.GuildManager
import com.carinaschoppe.skylife.guild.GuildRole
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Command handler for all guild-related commands.
 * Usage:
 * - /guild create <name> <tag>
 * - /guild invite <player>
 * - /guild kick <player>
 * - /guild promote <player>
 * - /guild leave
 * - /guild toggleff
 * - /guild info
 */
class GuildCommand : CommandExecutor, TabCompleter {

    private companion object {
        const val GUILD_NOT_FOUND = "Guild not found"
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: run {
            sender.sendMessage(Messages.ERROR_NOTPLAYER)
            return true
        }

        if (!player.hasPermission("skylife.guild")) {
            player.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        if (args.isEmpty()) {
            sendUsage(player)
            return true
        }

        handleSubcommand(player, args)
        return true
    }

    private fun handleSubcommand(player: Player, args: Array<out String>) {
        when (args[0].lowercase()) {
            "create" -> handleCreate(player, args)
            "invite" -> handleInvite(player, args)
            "kick" -> handleKick(player, args)
            "promote" -> handlePromote(player, args)
            "leave" -> handleLeave(player)
            "toggleff" -> handleToggleFriendlyFire(player)
            "info" -> handleInfo(player)
            else -> sendUsage(player)
        }
    }

    private fun handleCreate(player: Player, args: Array<out String>) {
        if (args.size < 3) {
            player.sendMessage(Messages.PREFIX.append(Component.text("Usage: /guild create <name> <tag>", Messages.ERROR_COLOR)))
            return
        }

        val name = args[1]
        val tag = args[2]

        val result = GuildManager.createGuild(name, tag, player.uniqueId)
        if (result.isSuccess) {
            player.sendMessage(
                Messages.PREFIX.append(Component.text("Guild ", Messages.MESSAGE_COLOR))
                    .append(Component.text(name, Messages.NAME_COLOR, TextDecoration.BOLD))
                    .append(Component.text(" created with tag ", Messages.MESSAGE_COLOR))
                    .append(Component.text("[$tag]", Messages.ACCENT_COLOR, TextDecoration.BOLD))
            )
        } else {
            player.sendMessage(Messages.PREFIX.append(Component.text(result.exceptionOrNull()?.message ?: "Failed to create guild", Messages.ERROR_COLOR)))
        }
    }

    private fun handleInvite(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(Messages.PREFIX.append(Component.text("Usage: /guild invite <player>", Messages.ERROR_COLOR)))
            return
        }

        val guildId = GuildManager.getPlayerGuildId(player.uniqueId)
        if (guildId == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("You are not in a guild", Messages.ERROR_COLOR)))
            return
        }

        val target = Bukkit.getPlayerExact(args[1])
        if (target == null) {
            player.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND)
            return
        }

        val result = GuildManager.invitePlayer(guildId, player.uniqueId, target.uniqueId)
        if (result.isSuccess) {
            val guild = GuildManager.getGuild(guildId) ?: run {
                player.sendMessage(Messages.PREFIX.append(Component.text(GUILD_NOT_FOUND, Messages.ERROR_COLOR)))
                return
            }
            player.sendMessage(
                Messages.PREFIX.append(Component.text(target.name, Messages.NAME_COLOR, TextDecoration.BOLD))
                    .append(Component.text(" has been invited to the guild", Messages.MESSAGE_COLOR))
            )
            target.sendMessage(
                Messages.PREFIX.append(Component.text("You have been invited to guild ", Messages.MESSAGE_COLOR))
                    .append(Component.text(guild.name, Messages.NAME_COLOR, TextDecoration.BOLD))
            )

            // Update display name for new member
            updatePlayerDisplayName(target)
        } else {
            player.sendMessage(Messages.PREFIX.append(Component.text(result.exceptionOrNull()?.message ?: "Failed to invite player", Messages.ERROR_COLOR)))
        }
    }

    private fun handleKick(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(Messages.PREFIX.append(Component.text("Usage: /guild kick <player>", Messages.ERROR_COLOR)))
            return
        }

        val guildId = GuildManager.getPlayerGuildId(player.uniqueId)
        if (guildId == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("You are not in a guild", Messages.ERROR_COLOR)))
            return
        }

        val target = Bukkit.getPlayerExact(args[1])
        if (target == null) {
            player.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND)
            return
        }

        val result = GuildManager.kickPlayer(guildId, player.uniqueId, target.uniqueId)
        if (result.isSuccess) {
            val guild = GuildManager.getGuild(guildId)
            if (guild != null) {
                // Notify all guild members
                Bukkit.getOnlinePlayers().filter { GuildManager.getPlayerGuildId(it.uniqueId) == guildId }.forEach { member ->
                    member.sendMessage(
                        Messages.PREFIX.append(Component.text(target.name, Messages.NAME_COLOR, TextDecoration.BOLD))
                            .append(Component.text(" has been kicked from the guild", Messages.MESSAGE_COLOR))
                    )
                }
            }
            target.sendMessage(Messages.PREFIX.append(Component.text("You have been kicked from the guild", Messages.ERROR_COLOR)))

            // Update display name for kicked player
            updatePlayerDisplayName(target)
        } else {
            player.sendMessage(Messages.PREFIX.append(Component.text(result.exceptionOrNull()?.message ?: "Failed to kick player", Messages.ERROR_COLOR)))
        }
    }

    private fun handlePromote(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(Messages.PREFIX.append(Component.text("Usage: /guild promote <player>", Messages.ERROR_COLOR)))
            return
        }

        val guildId = GuildManager.getPlayerGuildId(player.uniqueId)
        if (guildId == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("You are not in a guild", Messages.ERROR_COLOR)))
            return
        }

        val target = Bukkit.getPlayerExact(args[1])
        if (target == null) {
            player.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND)
            return
        }

        val guild = GuildManager.getGuild(guildId) ?: run {
            player.sendMessage(Messages.PREFIX.append(Component.text(GUILD_NOT_FOUND, Messages.ERROR_COLOR)))
            return
        }
        guild.members[target.uniqueId]

        val result = GuildManager.promotePlayer(guildId, player.uniqueId, target.uniqueId)
        if (result.isSuccess) {
            val newRole = guild.members[target.uniqueId]
            val roleText = when (newRole) {
                GuildRole.LEADER -> "Leader"
                GuildRole.ELDER -> "Elder"
                else -> "Member"
            }

            // Notify all guild members
            Bukkit.getOnlinePlayers().filter { GuildManager.getPlayerGuildId(it.uniqueId) == guildId }.forEach { member ->
                member.sendMessage(
                    Messages.PREFIX.append(Component.text(target.name, Messages.NAME_COLOR, TextDecoration.BOLD))
                        .append(Component.text(" has been promoted to ", Messages.MESSAGE_COLOR))
                        .append(Component.text(roleText, Messages.ACCENT_COLOR, TextDecoration.BOLD))
                )
            }
        } else {
            player.sendMessage(Messages.PREFIX.append(Component.text(result.exceptionOrNull()?.message ?: "Failed to promote player", Messages.ERROR_COLOR)))
        }
    }

    private fun handleLeave(player: Player) {
        val guildId = GuildManager.getPlayerGuildId(player.uniqueId)
        if (guildId == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("You are not in a guild", Messages.ERROR_COLOR)))
            return
        }

        val guild = GuildManager.getGuild(guildId)
        val guildName = guild?.name ?: "Unknown"

        val result = GuildManager.leaveGuild(player.uniqueId)
        if (result.isSuccess) {
            player.sendMessage(
                Messages.PREFIX.append(Component.text("You have left the guild ", Messages.MESSAGE_COLOR))
                    .append(Component.text(guildName, Messages.NAME_COLOR, TextDecoration.BOLD))
            )

            // Notify remaining guild members
            Bukkit.getOnlinePlayers().filter { GuildManager.getPlayerGuildId(it.uniqueId) == guildId }.forEach { member ->
                member.sendMessage(
                    Messages.PREFIX.append(Component.text(player.name, Messages.NAME_COLOR, TextDecoration.BOLD))
                        .append(Component.text(" has left the guild", Messages.MESSAGE_COLOR))
                )
            }

            // Update display name
            updatePlayerDisplayName(player)
        } else {
            player.sendMessage(Messages.PREFIX.append(Component.text(result.exceptionOrNull()?.message ?: "Failed to leave guild", Messages.ERROR_COLOR)))
        }
    }

    private fun handleToggleFriendlyFire(player: Player) {
        val guildId = GuildManager.getPlayerGuildId(player.uniqueId)
        if (guildId == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("You are not in a guild", Messages.ERROR_COLOR)))
            return
        }

        val result = GuildManager.toggleFriendlyFire(guildId, player.uniqueId)
        if (result.isSuccess) {
            val enabled = result.getOrNull() ?: false
            val statusText = if (enabled) "enabled" else "disabled"
            val statusColor = if (enabled) Messages.ERROR_COLOR else Messages.MESSAGE_COLOR

            // Notify all guild members
            Bukkit.getOnlinePlayers().filter { GuildManager.getPlayerGuildId(it.uniqueId) == guildId }.forEach { member ->
                member.sendMessage(
                    Messages.PREFIX.append(Component.text("Friendly fire has been ", Messages.MESSAGE_COLOR))
                        .append(Component.text(statusText, statusColor, TextDecoration.BOLD))
                )
            }
        } else {
            player.sendMessage(Messages.PREFIX.append(Component.text(result.exceptionOrNull()?.message ?: "Failed to toggle friendly fire", Messages.ERROR_COLOR)))
        }
    }

    private fun handleInfo(player: Player) {
        val guildId = GuildManager.getPlayerGuildId(player.uniqueId)
        if (guildId == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text("You are not in a guild", Messages.ERROR_COLOR)))
            return
        }

        val guild = GuildManager.getGuild(guildId)
        if (guild == null) {
            player.sendMessage(Messages.PREFIX.append(Component.text(GUILD_NOT_FOUND, Messages.ERROR_COLOR)))
            return
        }

        val leaderName = Bukkit.getOfflinePlayer(guild.leaderUUID).name ?: "Unknown"
        val ffStatus = if (guild.friendlyFireEnabled) "Enabled" else "Disabled"

        player.sendMessage(Component.text("--- Guild Info ---", Messages.ACCENT_COLOR, TextDecoration.BOLD))
        player.sendMessage(
            Component.text("Name: ", Messages.MESSAGE_COLOR)
                .append(Component.text(guild.name, Messages.NAME_COLOR, TextDecoration.BOLD))
        )
        player.sendMessage(
            Component.text("Tag: ", Messages.MESSAGE_COLOR)
                .append(Component.text("[${guild.tag}]", Messages.ACCENT_COLOR, TextDecoration.BOLD))
        )
        player.sendMessage(
            Component.text("Leader: ", Messages.MESSAGE_COLOR)
                .append(Component.text(leaderName, Messages.NAME_COLOR, TextDecoration.BOLD))
        )
        player.sendMessage(
            Component.text("Friendly Fire: ", Messages.MESSAGE_COLOR)
                .append(Component.text(ffStatus, if (guild.friendlyFireEnabled) Messages.ERROR_COLOR else Messages.MESSAGE_COLOR))
        )
        player.sendMessage(Component.text("Members (${guild.members.size}):", Messages.MESSAGE_COLOR, TextDecoration.BOLD))

        guild.members.entries.sortedBy { it.value.ordinal }.forEach { (uuid, role) ->
            val memberName = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown"
            val roleText = when (role) {
                GuildRole.LEADER -> "[Leader]"
                GuildRole.ELDER -> "[Elder]"
                GuildRole.MEMBER -> ""
            }
            val roleColor = when (role) {
                GuildRole.LEADER -> Messages.ACCENT_COLOR
                GuildRole.ELDER -> NamedTextColor.YELLOW
                GuildRole.MEMBER -> Messages.GRAY_COLOR
            }

            player.sendMessage(
                Component.text("  - ", Messages.GRAY_COLOR)
                    .append(Component.text(memberName, Messages.NAME_COLOR))
                    .append(Component.text(" $roleText", roleColor))
            )
        }
    }

    private fun sendUsage(player: Player) {
        player.sendMessage(Component.text("--- Guild Commands ---", Messages.ACCENT_COLOR, TextDecoration.BOLD))
        player.sendMessage(Component.text("/guild create <name> <tag>", Messages.MESSAGE_COLOR))
        player.sendMessage(Component.text("/guild invite <player>", Messages.MESSAGE_COLOR))
        player.sendMessage(Component.text("/guild kick <player>", Messages.MESSAGE_COLOR))
        player.sendMessage(Component.text("/guild promote <player>", Messages.MESSAGE_COLOR))
        player.sendMessage(Component.text("/guild leave", Messages.MESSAGE_COLOR))
        player.sendMessage(Component.text("/guild toggleff", Messages.MESSAGE_COLOR))
        player.sendMessage(Component.text("/guild info", Messages.MESSAGE_COLOR))
    }

    private fun updatePlayerDisplayName(player: Player) {
        val tag = GuildManager.getFormattedTag(player.uniqueId)
        if (tag != null) {
            player.displayName(Component.text(tag, Messages.ACCENT_COLOR).append(Component.text(" ${player.name}", NamedTextColor.WHITE)))
        } else {
            player.displayName(Component.text(player.name, NamedTextColor.WHITE))
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (sender !is Player) return emptyList()

        return when (args.size) {
            1 -> listOf("create", "invite", "kick", "promote", "leave", "toggleff", "info")
                .filter { it.startsWith(args[0].lowercase()) }

            2 -> when (args[0].lowercase()) {
                "invite", "kick", "promote" -> Bukkit.getOnlinePlayers()
                    .map { it.name }
                    .filter { it.lowercase().startsWith(args[1].lowercase()) }

                else -> emptyList()
            }

            else -> emptyList()
        }
    }
}
