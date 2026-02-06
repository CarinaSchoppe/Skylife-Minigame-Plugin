package com.carinaschoppe.skylife.commands

import com.carinaschoppe.skylife.utility.VanishManager
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Command handler for vanish functionality.
 * Allows administrators and moderators to become invisible to other players.
 *
 * Usage:
 * - /vanish - Toggle vanish for yourself
 * - /vanish <player> - Toggle vanish for another player (requires skylife.vanish.others)
 *
 * Permissions:
 * - skylife.vanish.self - Toggle vanish for yourself
 * - skylife.vanish.others - Toggle vanish for other players
 * - skylife.vanish.see - See vanished players
 *
 * Features:
 * - Hides player from tab list and player list
 * - Enables fly mode automatically
 * - Restores previous fly state when unvanished
 * - Tab completion hides vanished players
 * - Auto-unvanish on game join, death, or quit
 */
class VanishCommand : CommandExecutor, TabCompleter {

    private companion object {
        const val PERMISSION_VANISH_OTHERS = "skylife.vanish.others"
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Messages.ONLY_PLAYERS)
            return true
        }

        // Check base permission
        if (!sender.hasPermission("skylife.vanish.self") && !sender.hasPermission(PERMISSION_VANISH_OTHERS)) {
            sender.sendMessage(Messages.NO_PERMISSION)
            return true
        }

        // /vanish - toggle own vanish
        if (args.isEmpty()) {
            handleSelfToggle(sender)
            return true
        }

        // /vanish <player> - toggle vanish for target player
        if (args.size == 1) {
            handleTargetToggle(sender, args[0])
            return true
        }

        sender.sendMessage(Messages.VANISH_USAGE)
        return true
    }

    private fun handleSelfToggle(player: Player) {
        if (!player.hasPermission("skylife.vanish.self")) {
            player.sendMessage(Messages.NO_PERMISSION)
            return
        }

        val isNowVanished = VanishManager.toggleVanish(player)
        if (isNowVanished) {
            player.sendMessage(Messages.VANISH_ENABLED)
        } else {
            player.sendMessage(Messages.VANISH_DISABLED)
        }
    }

    private fun handleTargetToggle(sender: Player, targetName: String) {
        if (!sender.hasPermission(PERMISSION_VANISH_OTHERS)) {
            sender.sendMessage(Messages.NO_PERMISSION)
            return
        }

        val target = Bukkit.getPlayer(targetName)
        if (target == null) {
            sender.sendMessage(Messages.PLAYER_NOT_ONLINE(targetName))
            return
        }

        val isNowVanished = VanishManager.toggleVanish(target)
        if (isNowVanished) {
            sender.sendMessage(Messages.VANISH_ENABLED_OTHER(target.name))
            target.sendMessage(Messages.VANISH_ENABLED)
        } else {
            sender.sendMessage(Messages.VANISH_DISABLED_OTHER(target.name))
            target.sendMessage(Messages.VANISH_DISABLED)
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (sender !is Player) return emptyList()

        if (args.size == 1 && sender.hasPermission(PERMISSION_VANISH_OTHERS)) {
            return Bukkit.getOnlinePlayers()
                .filter { player ->
                    // Don't show vanished players in tab complete unless sender can see them
                    !VanishManager.isVanished(player) || sender.hasPermission("skylife.vanish.see")
                }
                .map { it.name }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
        }

        return emptyList()
    }
}
