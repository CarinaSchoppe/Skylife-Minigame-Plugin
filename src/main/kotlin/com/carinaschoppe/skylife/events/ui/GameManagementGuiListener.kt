package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GameManagementGui
import com.carinaschoppe.skylife.utility.ui.SpawnManagementGui
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

/**
 * Listener for game management GUI interactions.
 */
class GameManagementGuiListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        val player = event.whoClicked as? Player ?: return

        when (holder) {
            is GameManagementGui.GameListHolder -> {
                event.isCancelled = true

                // Check permission
                if (!player.hasPermission("skylife.admin.managegames")) {
                    player.sendMessage(Messages.ERROR_PERMISSION)
                    player.closeInventory()
                    return
                }

                val gameName = GameManagementGui.getGameName(event.currentItem) ?: return
                val gamePattern = GameCluster.gamePatterns.firstOrNull { it.mapName == gameName } ?: return
                GameManagementGui.openEditGUI(player, gamePattern)
            }

            is GameManagementGui.GameEditHolder -> {
                event.isCancelled = true

                // Check permission
                if (!player.hasPermission("skylife.admin.managegames")) {
                    player.sendMessage(Messages.ERROR_PERMISSION)
                    player.closeInventory()
                    return
                }

                val gamePattern = holder.gamePattern
                val action = GameManagementGui.getAction(event.currentItem) ?: return

                when (action) {
                    "minPlayers-" -> {
                        if (gamePattern.minPlayers > 1) {
                            gamePattern.minPlayers--
                            player.sendMessage(
                                Messages.PREFIX
                                    .append(Component.text("Min Players decreased to ", Messages.MESSAGE_COLOR))
                                    .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
                            )
                            GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                        }
                    }

                    "minPlayers+" -> {
                        gamePattern.minPlayers++
                        if (gamePattern.maxPlayers < gamePattern.minPlayers) {
                            gamePattern.maxPlayers = gamePattern.minPlayers
                        }
                        player.sendMessage(
                            Messages.PREFIX
                                .append(Component.text("Min Players increased to ", Messages.MESSAGE_COLOR))
                                .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
                        )
                        GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                    }

                    "maxPlayers-" -> {
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
                            GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                        }
                    }

                    "maxPlayers+" -> {
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
                        GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                    }

                    "minPlayersToStart-" -> {
                        if (gamePattern.minPlayersToStart > 1) {
                            gamePattern.minPlayersToStart--
                            player.sendMessage(
                                Messages.PREFIX
                                    .append(Component.text("Min Players to Start decreased to ", Messages.MESSAGE_COLOR))
                                    .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
                            )
                            GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                        }
                    }

                    "minPlayersToStart+" -> {
                        if (gamePattern.minPlayersToStart < gamePattern.maxPlayers) {
                            gamePattern.minPlayersToStart++
                            player.sendMessage(
                                Messages.PREFIX
                                    .append(Component.text("Min Players to Start increased to ", Messages.MESSAGE_COLOR))
                                    .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
                            )
                            GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                        } else {
                            player.sendMessage(
                                Messages.PREFIX
                                    .append(Component.text("Min Players to Start cannot exceed Max Players (", Messages.ERROR_COLOR))
                                    .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
                                    .append(Component.text(")!", Messages.ERROR_COLOR))
                            )
                        }
                    }

                    "save" -> {
                        // Save the pattern to file
                        GameLoader.saveGameToFile(gamePattern)
                        player.sendMessage(
                            Messages.PREFIX
                                .append(Component.text("Game pattern '", Messages.MESSAGE_COLOR))
                                .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                                .append(Component.text("' saved successfully!", Messages.MESSAGE_COLOR))
                        )
                        player.closeInventory()
                        GameManagementGui.openListGUI(player)
                    }

                    "back" -> {
                        GameManagementGui.openListGUI(player)
                    }

                    "set_lobby" -> {
                        gamePattern.gameLocationManager.lobbyLocation =
                            GameLocationManager.locationToSkylifeLocationConverter(player.location)
                        GameLoader.saveGameToFile(gamePattern)
                        player.sendMessage(
                            Messages.PREFIX.append(
                                Component.text("Lobby location set to your current position!", Messages.MESSAGE_COLOR)
                            )
                        )
                        GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                    }

                    "set_spectator" -> {
                        gamePattern.gameLocationManager.spectatorLocation =
                            GameLocationManager.locationToSkylifeLocationConverter(player.location)
                        GameLoader.saveGameToFile(gamePattern)
                        player.sendMessage(
                            Messages.PREFIX.append(
                                Component.text("Spectator location set to your current position!", Messages.MESSAGE_COLOR)
                            )
                        )
                        GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                    }

                    "set_main" -> {
                        gamePattern.gameLocationManager.mainLocation =
                            GameLocationManager.locationToSkylifeLocationConverter(player.location)
                        GameLoader.saveGameToFile(gamePattern)
                        player.sendMessage(
                            Messages.PREFIX.append(
                                Component.text("Main location set to your current position!", Messages.MESSAGE_COLOR)
                            )
                        )
                        GameManagementGui.updateEditGUI(event.inventory, gamePattern)
                    }

                    "manage_spawns" -> {
                        SpawnManagementGui.openSpawnGUI(player, gamePattern)
                    }

                    "delete" -> {
                        // Stop all active games based on this pattern
                        val activeGames = GameCluster.activeGamesList.filter { it.pattern == gamePattern }
                        val lobbyGames = GameCluster.lobbyGamesList.filter { it.pattern == gamePattern }

                        activeGames.forEach { game ->
                            GameCluster.stopGame(game)
                        }

                        lobbyGames.forEach { game ->
                            game.getAllPlayers().forEach { p ->
                                GameCluster.removePlayerFromGame(p)
                            }
                        }

                        // Remove from cluster and delete file
                        GameCluster.gamePatterns.remove(gamePattern)
                        GameLoader.deleteGameFile(gamePattern)

                        player.sendMessage(
                            Messages.PREFIX
                                .append(Component.text("Game pattern '", Messages.MESSAGE_COLOR))
                                .append(Component.text(gamePattern.mapName, Messages.NAME_COLOR))
                                .append(Component.text("' has been deleted!", Messages.MESSAGE_COLOR))
                        )
                        player.closeInventory()

                        if (GameCluster.gamePatterns.isNotEmpty()) {
                            GameManagementGui.openListGUI(player)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is GameManagementGui.GameListHolder || holder is GameManagementGui.GameEditHolder) {
            event.isCancelled = true
        }
    }
}
