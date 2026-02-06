package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.managers.MapManager
import com.carinaschoppe.skylife.hub.HubManager
import com.carinaschoppe.skylife.utility.scoreboard.LobbyScoreboardManager
import com.carinaschoppe.skylife.utility.scoreboard.ScoreboardManager
import com.carinaschoppe.skylife.utility.ui.ExitDoorItem
import com.carinaschoppe.skylife.utility.ui.GameOverviewItems
import com.carinaschoppe.skylife.utility.ui.SkillsGui
import org.bukkit.entity.Player

interface PlayerSessionService {
    fun prepareForGameJoin(player: Player, game: Game)
    fun attachGameScoreboard(player: Player, game: Game)
    fun updateGameScoreboards(game: Game)
    fun resetToHubFromGame(player: Player)
    fun resetToHubAfterGameStop(player: Player)
}

class DefaultPlayerSessionService : PlayerSessionService {
    override fun prepareForGameJoin(player: Player, game: Game) {
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4)

        // Teleport to lobby in the game's dedicated world
        val lobbyInGameWorld = MapManager.locationWorldConverter(game.lobbyLocation, game)
        player.teleport(lobbyInGameWorld)

        // Add exit door to inventory
        player.inventory.setItem(8, ExitDoorItem.create())
    }

    override fun attachGameScoreboard(player: Player, game: Game) {
        ScoreboardManager.setScoreboard(player, game)
    }

    override fun updateGameScoreboards(game: Game) {
        game.getAllPlayers().forEach { ScoreboardManager.updateScoreboard(it, game) }
    }

    override fun resetToHubFromGame(player: Player) {
        ScoreboardManager.removeScoreboard(player)
        resetInventoryForHub(player)
        HubManager.teleportToHub(player)
        LobbyScoreboardManager.setLobbyScoreboard(player)
    }

    override fun resetToHubAfterGameStop(player: Player) {
        resetInventoryForHub(player)
        HubManager.teleportToHub(player)
        ScoreboardManager.removeScoreboard(player)
        LobbyScoreboardManager.setLobbyScoreboard(player)
    }

    private fun resetInventoryForHub(player: Player) {
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4)
        if (player.hasPermission("skylife.overview")) {
            player.inventory.setItem(0, GameOverviewItems.createMenuItem())
        }
        player.inventory.setItem(4, SkillsGui.createSkillsMenuItem())
    }
}
