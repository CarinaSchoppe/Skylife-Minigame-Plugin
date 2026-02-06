package com.carinaschoppe.skylife.events.ui

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.GameLoader
import com.carinaschoppe.skylife.game.GamePattern
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
import org.bukkit.inventory.Inventory

/**
 * Listener for game management GUI interactions.
 */
class GameManagementGuiListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        when (val holder = event.inventory.holder) {
            is GameManagementGui.GameListHolder -> handleListClick(event, player)
            is GameManagementGui.GameEditHolder -> handleEditClick(event, player, holder)
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is GameManagementGui.GameListHolder || holder is GameManagementGui.GameEditHolder) {
            event.isCancelled = true
        }
    }

    private fun handleListClick(event: InventoryClickEvent, player: Player) {
        event.isCancelled = true
        if (!ensureManagePermission(player)) return

        val gameName = GameManagementGui.getGameName(event.currentItem) ?: return
        val gamePattern = GameCluster.gamePatterns.firstOrNull { it.mapName == gameName } ?: return
        GameManagementGui.openEditGUI(player, gamePattern)
    }

    private fun handleEditClick(event: InventoryClickEvent, player: Player, holder: GameManagementGui.GameEditHolder) {
        event.isCancelled = true
        if (!ensureManagePermission(player)) return

        val gamePattern = holder.gamePattern
        val action = GameManagementGui.getAction(event.currentItem) ?: return

        when (action) {
            "minPlayers-" -> decreaseMinPlayers(player, gamePattern, event.inventory)
            "minPlayers+" -> increaseMinPlayers(player, gamePattern, event.inventory)
            "maxPlayers-" -> decreaseMaxPlayers(player, gamePattern, event.inventory)
            "maxPlayers+" -> increaseMaxPlayers(player, gamePattern, event.inventory)
            "minPlayersToStart-" -> decreaseMinPlayersToStart(player, gamePattern, event.inventory)
            "minPlayersToStart+" -> increaseMinPlayersToStart(player, gamePattern, event.inventory)
            "save" -> savePattern(player, gamePattern)
            "back" -> GameManagementGui.openListGUI(player)
            "set_lobby" -> setLobbyLocation(player, gamePattern, event.inventory)
            "set_spectator" -> setSpectatorLocation(player, gamePattern, event.inventory)
            "set_main" -> setMainLocation(player, gamePattern, event.inventory)
            "manage_spawns" -> SpawnManagementGui.openSpawnGUI(player, gamePattern)
            "delete" -> deletePattern(player, gamePattern)
        }
    }

    private fun ensureManagePermission(player: Player): Boolean {
        if (!player.hasPermission("skylife.admin.managegames")) {
            player.sendMessage(Messages.ERROR_PERMISSION)
            player.closeInventory()
            return false
        }
        return true
    }

    private fun decreaseMinPlayers(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        if (gamePattern.minPlayers <= 1) return
        gamePattern.minPlayers--
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players decreased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun increaseMinPlayers(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        gamePattern.minPlayers++
        if (gamePattern.maxPlayers < gamePattern.minPlayers) {
            gamePattern.maxPlayers = gamePattern.minPlayers
        }
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players increased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.minPlayers.toString(), Messages.NAME_COLOR))
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun decreaseMaxPlayers(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        if (gamePattern.maxPlayers <= gamePattern.minPlayers) return
        gamePattern.maxPlayers--
        if (gamePattern.minPlayersToStart > gamePattern.maxPlayers) {
            gamePattern.minPlayersToStart = gamePattern.maxPlayers
        }
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Max Players decreased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun increaseMaxPlayers(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        gamePattern.maxPlayers++
        if (gamePattern.minPlayersToStart > gamePattern.maxPlayers) {
            gamePattern.minPlayersToStart = gamePattern.maxPlayers
        }
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Max Players increased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun decreaseMinPlayersToStart(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        if (gamePattern.minPlayersToStart <= 1) return
        gamePattern.minPlayersToStart--
        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players to Start decreased to ", Messages.MESSAGE_COLOR))
                .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun increaseMinPlayersToStart(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        if (gamePattern.minPlayersToStart < gamePattern.maxPlayers) {
            gamePattern.minPlayersToStart++
            player.sendMessage(
                Messages.PREFIX
                    .append(Component.text("Min Players to Start increased to ", Messages.MESSAGE_COLOR))
                    .append(Component.text(gamePattern.minPlayersToStart.toString(), Messages.NAME_COLOR))
            )
            GameManagementGui.updateEditGUI(inventory, gamePattern)
            return
        }

        player.sendMessage(
            Messages.PREFIX
                .append(Component.text("Min Players to Start cannot exceed Max Players (", Messages.ERROR_COLOR))
                .append(Component.text(gamePattern.maxPlayers.toString(), Messages.NAME_COLOR))
                .append(Component.text(")!", Messages.ERROR_COLOR))
        )
    }

    private fun savePattern(player: Player, gamePattern: GamePattern) {
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

    private fun setLobbyLocation(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        gamePattern.gameLocationManager.lobbyLocation =
            GameLocationManager.locationToSkylifeLocationConverter(player.location)
        GameLoader.saveGameToFile(gamePattern)
        player.sendMessage(
            Messages.PREFIX.append(
                Component.text("Lobby location set to your current position!", Messages.MESSAGE_COLOR)
            )
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun setSpectatorLocation(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        gamePattern.gameLocationManager.spectatorLocation =
            GameLocationManager.locationToSkylifeLocationConverter(player.location)
        GameLoader.saveGameToFile(gamePattern)
        player.sendMessage(
            Messages.PREFIX.append(
                Component.text("Spectator location set to your current position!", Messages.MESSAGE_COLOR)
            )
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun setMainLocation(player: Player, gamePattern: GamePattern, inventory: Inventory) {
        gamePattern.gameLocationManager.mainLocation =
            GameLocationManager.locationToSkylifeLocationConverter(player.location)
        GameLoader.saveGameToFile(gamePattern)
        player.sendMessage(
            Messages.PREFIX.append(
                Component.text("Main location set to your current position!", Messages.MESSAGE_COLOR)
            )
        )
        GameManagementGui.updateEditGUI(inventory, gamePattern)
    }

    private fun deletePattern(player: Player, gamePattern: GamePattern) {
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

        GameCluster.removeGamePattern(gamePattern)
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
