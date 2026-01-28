package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.managers.GameLocationManager
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.ui.GameManagementGui
import com.carinaschoppe.skylife.utility.ui.SpawnManagementGui
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

/**
 * Listener for spawn management GUI interactions.
 */
class SpawnManagementGuiListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder as? SpawnManagementGui.SpawnHolder ?: return
        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        val gamePattern = holder.gamePattern
        val clickedItem = event.currentItem ?: return

        // Check for action buttons
        val action = SpawnManagementGui.getAction(clickedItem)
        if (action != null) {
            when (action) {
                "add_spawn" -> {
                    val skylifeLocation = GameLocationManager.locationToSkylifeLocationConverter(player.location)
                    gamePattern.gameLocationManager.spawnLocations.add(skylifeLocation)
                    val spawnNumber = gamePattern.gameLocationManager.spawnLocations.size

                    // Save immediately
                    GameLoader.saveGameToFile(gamePattern)

                    player.sendMessage(
                        Messages.PREFIX
                            .append(Component.text("Added spawn location #", Messages.MESSAGE_COLOR))
                            .append(Component.text(spawnNumber.toString(), Messages.NAME_COLOR))
                            .append(Component.text("!", Messages.MESSAGE_COLOR))
                    )
                    SpawnManagementGui.updateSpawnGUI(event.inventory, gamePattern, player)
                }

                "back" -> {
                    GameManagementGui.openEditGUI(player, gamePattern)
                }
            }
            return
        }

        // Check for spawn items
        val spawnIndex = SpawnManagementGui.getSpawnIndex(clickedItem) ?: return

        when (event.click) {
            ClickType.LEFT -> {
                // Teleport to spawn
                val spawn = gamePattern.gameLocationManager.spawnLocations.getOrNull(spawnIndex)
                if (spawn != null) {
                    val location = GameLocationManager.skylifeLocationToLocationConverter(spawn)
                    if (location != null) {
                        player.teleport(location)
                        player.sendMessage(
                            Messages.PREFIX
                                .append(Component.text("Teleported to spawn #", Messages.MESSAGE_COLOR))
                                .append(Component.text((spawnIndex + 1).toString(), Messages.NAME_COLOR))
                        )
                    } else {
                        player.sendMessage(
                            Messages.PREFIX
                                .append(Component.text("Could not teleport - world not loaded!", Messages.ERROR_COLOR))
                        )
                    }
                }
            }

            ClickType.RIGHT -> {
                // Delete spawn
                if (spawnIndex < gamePattern.gameLocationManager.spawnLocations.size) {
                    gamePattern.gameLocationManager.spawnLocations.removeAt(spawnIndex)

                    // Save immediately
                    GameLoader.saveGameToFile(gamePattern)

                    player.sendMessage(
                        Messages.PREFIX
                            .append(Component.text("Removed spawn location #", Messages.MESSAGE_COLOR))
                            .append(Component.text((spawnIndex + 1).toString(), Messages.NAME_COLOR))
                    )
                    SpawnManagementGui.updateSpawnGUI(event.inventory, gamePattern, player)
                }
            }

            else -> {
                // Ignore other click types
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is SpawnManagementGui.SpawnHolder) {
            event.isCancelled = true
        }
    }
}
