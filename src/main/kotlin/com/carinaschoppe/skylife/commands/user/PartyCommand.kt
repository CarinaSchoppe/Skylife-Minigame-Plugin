package com.carinaschoppe.skylife.commands.user

import com.carinaschoppe.skylife.party.PartyManager
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

/**
 * Command for managing parties.
 * Usage:
 * /party create - Creates a new party
 * /party invite <player> - Invites a player to your party
 * /party accept <player> - Accepts a party invite from a player
 * /party leave - Leaves your current party
 * /party kick <player> - Kicks a player from your party (leader only)
 * /party promote <player> - Promotes a player to party leader
 * /party list - Lists all party members
 * /party invites - Lists all pending invites
 */
class PartyCommand : TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Messages.ERROR_NOTPLAYER())
            return true
        }

        if (!sender.hasPermission("skylife.party")) {
            sender.sendMessage(Messages.ERROR_PERMISSION)
            return true
        }

        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }

        when (args[0].lowercase()) {
            "create" -> handleCreate(sender)
            "invite" -> handleInvite(sender, args)
            "accept" -> handleAccept(sender, args)
            "leave" -> handleLeave(sender)
            "kick" -> handleKick(sender, args)
            "promote" -> handlePromote(sender, args)
            "list" -> handleList(sender)
            "invites" -> handleInvites(sender)
            else -> sendHelp(sender)
        }

        return true
    }

    private fun handleCreate(player: Player) {
        if (PartyManager.isInParty(player.uniqueId)) {
            player.sendMessage(Messages.PARTY_ALREADY_IN_PARTY)
            return
        }

        PartyManager.createParty(player.uniqueId)
        player.sendMessage(Messages.PARTY_CREATED)
    }

    private fun handleInvite(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(Messages.PARTY_INVITE_USAGE)
            return
        }

        val party = PartyManager.getPlayerParty(player.uniqueId)
        if (party == null) {
            player.sendMessage(Messages.PARTY_NOT_IN_PARTY)
            return
        }

        val targetName = args[1]
        val target = Bukkit.getPlayerExact(targetName)

        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_ONLINE(targetName))
            return
        }

        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(Messages.PARTY_CANNOT_INVITE_SELF)
            return
        }

        val result = PartyManager.invitePlayer(party.id, player.uniqueId, target.uniqueId)

        result.onSuccess {
            player.sendMessage(Messages.PARTY_INVITE_SENT(target.name))
            target.sendMessage(Messages.PARTY_INVITE_RECEIVED(player.name))
        }.onFailure { error ->
            player.sendMessage(Messages.parse("<red>${error.message}</red>"))
        }
    }

    private fun handleAccept(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(Messages.PARTY_ACCEPT_USAGE)
            return
        }

        val inviterName = args[1]
        val result = PartyManager.acceptInvite(player.uniqueId, inviterName)

        result.onSuccess { party ->
            val inviter = Bukkit.getPlayerExact(inviterName)

            player.sendMessage(Messages.PARTY_JOINED(inviter?.name ?: inviterName))

            // Notify all party members
            PartyManager.getOnlinePartyMembers(party).forEach { member ->
                if (member.uniqueId != player.uniqueId) {
                    member.sendMessage(Messages.PARTY_MEMBER_JOINED(player.name))
                }
            }
        }.onFailure { error ->
            player.sendMessage(Messages.parse("<red>${error.message}</red>"))
        }
    }

    private fun handleLeave(player: Player) {
        val party = PartyManager.getPlayerParty(player.uniqueId)
        if (party == null) {
            player.sendMessage(Messages.PARTY_NOT_IN_PARTY)
            return
        }

        val wasLeader = party.isLeader(player.uniqueId)
        val oldMembers = PartyManager.getOnlinePartyMembers(party).filter { it.uniqueId != player.uniqueId }

        val result = PartyManager.leaveParty(player.uniqueId)

        result.onSuccess {
            player.sendMessage(Messages.PARTY_LEFT)

            // Notify remaining members
            if (wasLeader && oldMembers.isNotEmpty()) {
                val newLeader = PartyManager.getPartyLeader(party)
                oldMembers.forEach { member ->
                    member.sendMessage(Messages.PARTY_MEMBER_LEFT(player.name))
                    if (newLeader != null && member.uniqueId == newLeader.uniqueId) {
                        member.sendMessage(Messages.PARTY_PROMOTED_TO_LEADER)
                    }
                }
            } else {
                oldMembers.forEach { member ->
                    member.sendMessage(Messages.PARTY_MEMBER_LEFT(player.name))
                }
            }
        }.onFailure { error ->
            player.sendMessage(Messages.parse("<red>${error.message}</red>"))
        }
    }

    private fun handleKick(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(Messages.PARTY_KICK_USAGE)
            return
        }

        val party = PartyManager.getPlayerParty(player.uniqueId)
        if (party == null) {
            player.sendMessage(Messages.PARTY_NOT_IN_PARTY)
            return
        }

        val targetName = args[1]
        val target = Bukkit.getPlayerExact(targetName) ?: Bukkit.getOfflinePlayer(targetName)

        val result = PartyManager.kickPlayer(party.id, player.uniqueId, target.uniqueId)

        result.onSuccess {
            player.sendMessage(Messages.PARTY_KICKED(target.name ?: "Unknown"))

            val onlineTarget = Bukkit.getPlayer(target.uniqueId)
            onlineTarget?.sendMessage(Messages.PARTY_KICKED_BY_LEADER)

            // Notify other party members
            PartyManager.getOnlinePartyMembers(party).forEach { member ->
                if (member.uniqueId != player.uniqueId && member.uniqueId != target.uniqueId) {
                    member.sendMessage(Messages.PARTY_MEMBER_KICKED(target.name ?: "Unknown"))
                }
            }
        }.onFailure { error ->
            player.sendMessage(Messages.parse("<red>${error.message}</red>"))
        }
    }

    private fun handlePromote(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(Messages.PARTY_PROMOTE_USAGE)
            return
        }

        val party = PartyManager.getPlayerParty(player.uniqueId)
        if (party == null) {
            player.sendMessage(Messages.PARTY_NOT_IN_PARTY)
            return
        }

        val targetName = args[1]
        val target = Bukkit.getPlayerExact(targetName)

        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_ONLINE(targetName))
            return
        }

        val result = PartyManager.promoteToLeader(party.id, player.uniqueId, target.uniqueId)

        result.onSuccess {
            player.sendMessage(Messages.PARTY_PROMOTED(target.name))
            target.sendMessage(Messages.PARTY_PROMOTED_TO_LEADER)

            // Notify other party members
            PartyManager.getOnlinePartyMembers(party).forEach { member ->
                if (member.uniqueId != player.uniqueId && member.uniqueId != target.uniqueId) {
                    member.sendMessage(Messages.PARTY_NEW_LEADER(target.name))
                }
            }
        }.onFailure { error ->
            player.sendMessage(Messages.parse("<red>${error.message}</red>"))
        }
    }

    private fun handleList(player: Player) {
        val party = PartyManager.getPlayerParty(player.uniqueId)
        if (party == null) {
            player.sendMessage(Messages.PARTY_NOT_IN_PARTY)
            return
        }

        val leader = PartyManager.getPartyLeader(party)
        val members = PartyManager.getOnlinePartyMembers(party)

        player.sendMessage(Messages.PARTY_LIST_HEADER)
        player.sendMessage(Messages.PARTY_LIST_LEADER(leader?.name ?: "Unknown"))

        members.forEach { member ->
            if (!party.isLeader(member.uniqueId)) {
                val status = if (member.isOnline) "<green>●</green>" else "<red>●</red>"
                player.sendMessage(Messages.PARTY_LIST_MEMBER(member.name, status))
            }
        }

        player.sendMessage(Messages.PARTY_LIST_FOOTER(party.size()))
    }

    private fun handleInvites(player: Player) {
        val invites = PartyManager.getPendingInvites(player.uniqueId)

        if (invites.isEmpty()) {
            player.sendMessage(Messages.PARTY_NO_INVITES)
            return
        }

        player.sendMessage(Messages.PARTY_INVITES_HEADER)
        invites.forEach { invite ->
            val inviter = Bukkit.getPlayer(invite.inviter)
            val inviterName = inviter?.name ?: "Unknown"
            val timeLeft = invite.getRemainingSeconds()
            player.sendMessage(Messages.PARTY_INVITE_ENTRY(inviterName, timeLeft))
        }
    }

    private fun sendHelp(player: Player) {
        player.sendMessage(Messages.PARTY_HELP_HEADER)
        player.sendMessage(Messages.PARTY_HELP_CREATE)
        player.sendMessage(Messages.PARTY_HELP_INVITE)
        player.sendMessage(Messages.PARTY_HELP_ACCEPT)
        player.sendMessage(Messages.PARTY_HELP_LEAVE)
        player.sendMessage(Messages.PARTY_HELP_KICK)
        player.sendMessage(Messages.PARTY_HELP_PROMOTE)
        player.sendMessage(Messages.PARTY_HELP_LIST)
        player.sendMessage(Messages.PARTY_HELP_INVITES)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (sender !is Player) return null

        return when (args.size) {
            1 -> {
                val subcommands = listOf("create", "invite", "accept", "leave", "kick", "promote", "list", "invites")
                subcommands.filter { it.startsWith(args[0].lowercase()) }
            }

            2 -> {
                when (args[0].lowercase()) {
                    "invite" -> {
                        // Suggest online players who are not in a party
                        Bukkit.getOnlinePlayers()
                            .filter { !PartyManager.isInParty(it.uniqueId) && it.uniqueId != sender.uniqueId }
                            .map { it.name }
                            .filter { it.lowercase().startsWith(args[1].lowercase()) }
                    }

                    "accept" -> {
                        // Suggest players who have invited this player
                        PartyManager.getPendingInvites(sender.uniqueId)
                            .mapNotNull { Bukkit.getPlayer(it.inviter)?.name }
                            .filter { it.lowercase().startsWith(args[1].lowercase()) }
                    }

                    "kick", "promote" -> {
                        // Suggest party members (except self)
                        val party = PartyManager.getPlayerParty(sender.uniqueId)
                        if (party != null) {
                            PartyManager.getOnlinePartyMembers(party)
                                .filter { it.uniqueId != sender.uniqueId }
                                .map { it.name }
                                .filter { it.lowercase().startsWith(args[1].lowercase()) }
                        } else {
                            emptyList()
                        }
                    }

                    else -> null
                }
            }

            else -> null
        }
    }
}
