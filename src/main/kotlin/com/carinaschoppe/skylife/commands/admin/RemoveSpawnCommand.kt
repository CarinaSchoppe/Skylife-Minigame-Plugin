package com.carinaschoppe.skylife.commands.admin

import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command to remove a spawn location from the game setup.
 * Usage: /removespawn <number>
 */
class RemoveSpawnCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Messages.PREFIX.append(Component.text("This command can only be used by players!", Messages.ERROR_COLOR)))
            return true
        }

        // Check if player has active setup
        val gamePattern = GameSetupCommand.activeSetups[sender]
        if (gamePattern == null) {
            sender.sendMessage(
                Messages.PREFIX.append(
                    Component.text("You don't have an active game setup! Use /skylife gamesetup <name> first.", Messages.ERROR_COLOR)
                )
            )
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(
                Messages.PREFIX.append(
                    Component.text("Usage: /removespawn <number>", Messages.ERROR_COLOR)
                )
            )
            return true
        }

        val spawnNumber = args[0].toIntOrNull()
        if (spawnNumber == null || spawnNumber < 1) {
            sender.sendMessage(
                Messages.PREFIX.append(
                    Component.text("Please provide a valid spawn number (1 or higher)!", Messages.ERROR_COLOR)
                )
            )
            return true
        }

        val spawnList = gamePattern.gameLocationManager.spawnLocations.toList()
        if (spawnNumber > spawnList.size) {
            sender.sendMessage(
                Messages.PREFIX
                    .append(Component.text("Spawn #", Messages.ERROR_COLOR))
                    .append(Component.text(spawnNumber.toString(), Messages.NAME_COLOR))
                    .append(Component.text(" doesn't exist! Current spawns: ", Messages.ERROR_COLOR))
                    .append(Component.text(spawnList.size.toString(), Messages.NAME_COLOR))
            )
            return true
        }

        // Remove the spawn at index (number - 1)
        val removedSpawn = spawnList[spawnNumber - 1]
        gamePattern.gameLocationManager.spawnLocations.remove(removedSpawn)

        sender.sendMessage(
            Messages.PREFIX
                .append(Component.text("Removed spawn #", Messages.MESSAGE_COLOR))
                .append(Component.text(spawnNumber.toString(), Messages.NAME_COLOR))
                .append(Component.text("! Remaining spawns: ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.gameLocationManager.spawnLocations.size.toString(), Messages.NAME_COLOR))
        )

        return true
    }
}
