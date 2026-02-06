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

        // Check permission
        if (!player.hasPermission("skylife.admin.gamesetup")) {
            player.sendMessage(Messages.ERROR_PERMISSION)
            player.closeInventory()
            return
        }

        val gamePattern = holder.gamePattern
        val slot = event.slot

        when (slot) {
            // Min Players controls
            GameSetupGui.MIN_PLAYERS_DECREASE -> handleMinPlayersDecrease(player, gamePattern, holder)
            GameSetupGui.MIN_PLAYERS_INCREASE -> handleMinPlayersIncrease(player, gamePattern, holder)

            // Max Players controls
            GameSetupGui.MAX_PLAYERS_DECREASE -> handleMaxPlayersDecrease(player, gamePattern, holder)
            GameSetupGui.MAX_PLAYERS_INCREASE -> handleMaxPlayersIncrease(player, gamePattern, holder)
            GameSetupGui.MIN_TO_START_DECREASE -> handleMinToStartDecrease(player, gamePattern, holder)
            GameSetupGui.MIN_TO_START_INCREASE -> handleMinToStartIncrease(player, gamePattern, holder)

            // Location setters
            GameSetupGui.LOBBY_LOCATION -> setLobbyLocation(player, gamePattern, holder)
            GameSetupGui.SPECTATOR_LOCATION -> setSpectatorLocation(player, gamePattern, holder)
            GameSetupGui.MAIN_LOCATION -> setMainLocation(player, gamePattern, holder)
            GameSetupGui.SPAWN_LOCATIONS -> addSpawnLocation(player, gamePattern, holder)

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
                    GameCluster.addGamePattern(gamePattern)
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

    private fun handleMinPlayersDecrease(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        if (gamePattern.minPlayers <= 1) {
            return
        }
        gamePattern.minPlayers--
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players decreased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
        )
        refreshInventory(holder)
    }

    private fun handleMinPlayersIncrease(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        gamePattern.minPlayers++
        if (gamePattern.maxPlayers < gamePattern.minPlayers) {
            gamePattern.maxPlayers = gamePattern.minPlayers
        }
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players increased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
        )
        refreshInventory(holder)
    }

    private fun handleMaxPlayersDecrease(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        if (gamePattern.maxPlayers <= gamePattern.minPlayers) {
            return
        }
        gamePattern.maxPlayers--
        if (gamePattern.minPlayersToStart > gamePattern.maxPlayers) {
            gamePattern.minPlayersToStart = gamePattern.maxPlayers
        }
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Max Players decreased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
        )
        refreshInventory(holder)
    }

    private fun handleMaxPlayersIncrease(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        gamePattern.maxPlayers++
        if (gamePattern.minPlayersToStart > gamePattern.maxPlayers) {
            gamePattern.minPlayersToStart = gamePattern.maxPlayers
        }
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Max Players increased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
        )
        refreshInventory(holder)
    }

    private fun handleMinToStartDecrease(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        if (gamePattern.minPlayersToStart <= 1) {
            return
        }
        gamePattern.minPlayersToStart--
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players to Start decreased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
        )
        refreshInventory(holder)
    }

    private fun handleMinToStartIncrease(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        if (gamePattern.minPlayersToStart < gamePattern.maxPlayers) {
            gamePattern.minPlayersToStart++
            player.sendMessage(
                Messages.PREFIX
                    .append(Component.text("Min Players to Start increased to ", Messages.MESSAGE_COLOR))
                    .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
            )
            refreshInventory(holder)
            return
        }

        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players to Start cannot exceed Max Players (", Messages.ERROR_COLOR))
                .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
                .append(Component.text(")!", Messages.ERROR_COLOR))
        )
    }

    private fun setLobbyLocation(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        gamePattern.gameLocationManager.lobbyLocation =
            GameLocationManager.locationToSkylifeLocationConverter(player.location)
        player.sendMessage(
            Messages.PREFIX.append(
                Component.text("Lobby location set to your current position!", Messages.MESSAGE_COLOR)
            )
        )
        refreshInventory(holder)
    }

    private fun setSpectatorLocation(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        gamePattern.gameLocationManager.spectatorLocation =
            GameLocationManager.locationToSkylifeLocationConverter(player.location)
        player.sendMessage(
            Messages.PREFIX.append(
                Component.text("Spectator location set to your current position!", Messages.MESSAGE_COLOR)
            )
        )
        refreshInventory(holder)
    }

    private fun setMainLocation(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        gamePattern.gameLocationManager.mainLocation =
            GameLocationManager.locationToSkylifeLocationConverter(player.location)
        player.sendMessage(
            Messages.PREFIX.append(
                Component.text("Main location set to your current position!", Messages.MESSAGE_COLOR)
            )
        )
        refreshInventory(holder)
    }

    private fun addSpawnLocation(player: Player, gamePattern: com.carinaschoppe.skylife.game.GamePattern, holder: GameSetupHolderFactory) {
        val skylifeLocation = GameLocationManager.locationToSkylifeLocationConverter(player.location)
        gamePattern.gameLocationManager.spawnLocations.add(skylifeLocation)
        val spawnNumber = gamePattern.gameLocationManager.spawnLocations.size
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Added spawn location #", Messages.MESSAGE_COLOR))
                .append(Component.text(spawnNumber.toString(), Messages.NAME_COLOR))
                .append(Component.text("!", Messages.MESSAGE_COLOR))
        )
        refreshInventory(holder)
    }

    private fun refreshInventory(holder: GameSetupHolderFactory) {
        (holder as? GameSetupGui)?.updateInventory()
    }
}
