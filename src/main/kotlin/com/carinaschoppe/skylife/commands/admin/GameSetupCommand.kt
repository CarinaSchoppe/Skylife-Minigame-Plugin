package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GameSetupGui
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command to start the game setup process with a GUI interface.
 * Usage: /skylife gamesetup <gamename>
 */
class GameSetupCommand : CommandExecutor {

    companion object {
        // Stores active setup sessions for players
        val activeSetups = mutableMapOf<Player, GamePattern>()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Messages.PREFIX.append(Component.text("This command can only be used by players!", Messages.ERROR_COLOR)))
            return true
        }

        // No args = open active setup GUI if exists
        if (args.isEmpty()) {
            val activePattern = activeSetups[sender]
            if (activePattern == null) {
                sender.sendMessage(
                    Messages.PREFIX.append(
                        Component.text("No active setup! Use /gamesetup <gamename> to start a new setup.", Messages.ERROR_COLOR)
                    )
                )
                return true
            }

            // Reopen the GUI for active setup
            val gui = GameSetupGui(sender, activePattern)
            sender.openInventory(gui.initInventory().inventory)
            return true
        }

        val gameName = args[0]

        // Check if player already has an active setup
        if (activeSetups.containsKey(sender)) {
            val currentSetup = activeSetups[sender]!!
            sender.sendMessage(
                Messages.PREFIX
                    .append(Component.text("You already have an active setup for '", Messages.ERROR_COLOR))
                    .append(Component.text(currentSetup.mapName, Messages.NAME_COLOR))
                    .append(Component.text("'! Finish it first with /game save or /game delete.", Messages.ERROR_COLOR))
            )
            return true
        }

        // Check if game pattern already exists in cluster
        if (GameCluster.gamePatterns.any { it.mapName.equals(gameName, ignoreCase = true) }) {
            sender.sendMessage(
                Messages.PREFIX
                    .append(Component.text("A game pattern with the name '", Messages.ERROR_COLOR))
                    .append(Component.text(gameName, Messages.NAME_COLOR))
                    .append(Component.text("' already exists!", Messages.ERROR_COLOR))
            )
            return true
        }

        // Create new game pattern
        val gamePattern = GamePattern(gameName)

        // Store the active setup
        activeSetups[sender] = gamePattern

        // Open the setup GUI
        val gui = GameSetupGui(sender, gamePattern)
        sender.openInventory(gui.initInventory().inventory)

        sender.sendMessage(
            Messages.PREFIX
                .append(Component.text("Starting setup for game: '", Messages.MESSAGE_COLOR))
                .append(Component.text(gameName, Messages.NAME_COLOR))
                .append(Component.text("'. Use commands or the GUI to configure it.", Messages.MESSAGE_COLOR))
        )

        return true
    }
}
