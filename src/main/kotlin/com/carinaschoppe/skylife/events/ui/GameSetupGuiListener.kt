package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.commands.admin.GameSetupCommand
import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GameSetupGui
import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameSetupHolderFactory
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

/**
 * Listener for game setup GUI interactions.
 */
class GameSetupGuiListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        if (holder !is GameSetupHolderFactory) return

        // Cancel all clicks to prevent item removal
        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        val gamePattern = holder.gamePattern
        val slot = event.slot

        when (slot) {
            // Min Players controls
            GameSetupGui.MIN_PLAYERS_DECREASE -> {
                if (gamePattern.minPlayers > 1) {
                    gamePattern.minPlayers--
                    player.sendMessage(
                        Messages.PREFIX
                            .append(Component.text("Min Players decreased to ", Messages.MESSAGE_COLOR))
                            .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
                    )
                    (holder as? GameSetupGui)?.updateInventory()
                }
            }

            GameSetupGui.MIN_PLAYERS_INCREASE -> {
                gamePattern.minPlayers++
                // Ensure max players is at least equal to min players
                if (gamePattern.maxPlayers < gamePattern.minPlayers) {
                    gamePattern.maxPlayers = gamePattern.minPlayers
                }
                player.sendMessage(
                    Messages.PREFIX
                        .append(Component.text("Min Players increased to ", Messages.MESSAGE_COLOR))
                        .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
                )
                (holder as? GameSetupGui)?.updateInventory()
            }

            // Max Players controls
            GameSetupGui.MAX_PLAYERS_DECREASE -> {
                if (gamePattern.maxPlayers > gamePattern.minPlayers) {
                    gamePattern.maxPlayers--
                    // Ensure minPlayersToStart doesn't exceed maxPlayers
                    if (gamePattern.minPlayersToStart > gamePattern.maxPlayers) {
                        gamePattern.minPlayersToStart = gamePattern.maxPlayers
                    }
                    player.sendMessage(
                        Messages.PREFIX
                            .append(Component.text("Max Players decreased to ", Messages.MESSAGE_COLOR))
                            .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
                    )
                    (holder as? GameSetupGui)?.updateInventory()
                }
            }

            GameSetupGui.MAX_PLAYERS_INCREASE -> {
                gamePattern.maxPlayers++
                // Ensure minPlayersToStart doesn't exceed maxPlayers
                if (gamePattern.minPlayersToStart > gamePattern.maxPlayers) {
                    gamePattern.minPlayersToStart = gamePattern.maxPlayers
                }
                player.sendMessage(
                    Messages.PREFIX
                        .append(Component.text("Max Players increased to ", Messages.MESSAGE_COLOR))
                        .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
                )
                (holder as? GameSetupGui)?.updateInventory()
            }

            GameSetupGui.MIN_TO_START_DECREASE -> {
                if (gamePattern.minPlayersToStart > 1) {
                    gamePattern.minPlayersToStart--
                    player.sendMessage(
                        Messages.PREFIX
                            .append(Component.text("Min Players to Start decreased to ", Messages.MESSAGE_COLOR))
                            .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
                    )
                    (holder as? GameSetupGui)?.updateInventory()
                }
            }

            GameSetupGui.MIN_TO_START_INCREASE -> {
                if (gamePattern.minPlayersToStart < gamePattern.maxPlayers) {
                    gamePattern.minPlayersToStart++
                    player.sendMessage(
                        Messages.PREFIX
                            .append(Component.text("Min Players to Start increased to ", Messages.MESSAGE_COLOR))
                            .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
                    )
                    (holder as? GameSetupGui)?.updateInventory()
                } else {
                    player.sendMessage(
                        Messages.PREFIX
                            .append(Component.text("Min Players to Start cannot exceed Max Players (", Messages.ERROR_COLOR))
                            .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
                            .append(Component.text(")!", Messages.ERROR_COLOR))
                    )
                }
            }

            // Location setters
            GameSetupGui.LOBBY_LOCATION -> {
                gamePattern.gameLocationManager.lobbyLocation =
                    GameLocationManager.locationToSkylifeLocationConverter(player.location)
                player.sendMessage(
                    Messages.PREFIX.append(
                        Component.text("Lobby location set to your current position!", Messages.MESSAGE_COLOR)
                    )
                )
                (holder as? GameSetupGui)?.updateInventory()
            }

            GameSetupGui.SPECTATOR_LOCATION -> {
                gamePattern.gameLocationManager.spectatorLocation =
                    GameLocationManager.locationToSkylifeLocationConverter(player.location)
                player.sendMessage(
                    Messages.PREFIX.append(
                        Component.text("Spectator location set to your current position!", Messages.MESSAGE_COLOR)
                    )
                )
                (holder as? GameSetupGui)?.updateInventory()
            }

            GameSetupGui.MAIN_LOCATION -> {
                gamePattern.gameLocationManager.mainLocation =
                    GameLocationManager.locationToSkylifeLocationConverter(player.location)
                player.sendMessage(
                    Messages.PREFIX.append(
                        Component.text("Main location set to your current position!", Messages.MESSAGE_COLOR)
                    )
                )
                (holder as? GameSetupGui)?.updateInventory()
            }

            GameSetupGui.SPAWN_LOCATIONS -> {
                val skylifeLocation = GameLocationManager.locationToSkylifeLocationConverter(player.location)
                gamePattern.gameLocationManager.spawnLocations.add(skylifeLocation)
                val spawnNumber = gamePattern.gameLocationManager.spawnLocations.size
                player.sendMessage(
                    Messages.PREFIX
                        .append(Component.text("Added spawn location #", Messages.MESSAGE_COLOR))
                        .append(Component.text(spawnNumber.toString(), Messages.NAME_COLOR))
                        .append(Component.text("!", Messages.MESSAGE_COLOR))
                )
                (holder as? GameSetupGui)?.updateInventory()
            }

            // Cancel button
            GameSetupGui.CANCEL_SLOT -> {
                // Remove from active setups
                GameSetupCommand.activeSetups.remove(player)

                player.sendMessage(
                    Messages.PREFIX
                        .append(Component.text("Setup for '", Messages.MESSAGE_COLOR))
                        .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                        .append(Component.text("' has been cancelled and discarded.", Messages.ERROR_COLOR))
                )

                // Close inventory
                player.closeInventory()
            }

            // Finish button
            GameSetupGui.FINISH_SLOT -> {
                if (gamePattern.isComplete()) {
                    // Save the pattern
                    GameCluster.gamePatterns.add(gamePattern)
                    GameLoader.saveGameToFile(gamePattern)

                    // Create a game instance from the pattern
                    try {
                        GameCluster.createGameFromPattern(gamePattern)
                        player.sendMessage(
                            Messages.PREFIX
                                .append(Component.text("Game '", Messages.MESSAGE_COLOR))
                                .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                                .append(Component.text("' is now available in the compass!", Messages.MESSAGE_COLOR))
                        )
                    } catch (e: IllegalStateException) {
                        player.sendMessage(
                            Messages.PREFIX
                                .append(Component.text("Game pattern saved, but failed to create game instance: ", Messages.ERROR_COLOR))
                                .append(Component.text(e.message ?: "Unknown error", Messages.ERROR_COLOR))
                        )
                    }

                    // Remove from active setups
                    GameSetupCommand.activeSetups.remove(player)

                    // Close inventory
                    player.closeInventory()
                } else {
                    player.sendMessage(
                        Messages.PREFIX.append(
                            Component.text("Please complete all requirements before finishing setup!", Messages.ERROR_COLOR)
                        )
                    )
                }
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is GameSetupHolderFactory) {
            // Cancel all drags to prevent item manipulation
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (holder is GameSetupHolderFactory) {
            val player = event.player as? Player ?: return

            // Check if setup was completed (removed from active setups)
            if (GameSetupCommand.activeSetups.containsKey(player)) {
                player.sendMessage(
                    Messages.PREFIX
                        .append(Component.text("Setup closed. You can reopen it with ", Messages.MESSAGE_COLOR))
                        .append(Component.text("/gamesetup", Messages.NAME_COLOR))
                )
            }
        }
    }
}
