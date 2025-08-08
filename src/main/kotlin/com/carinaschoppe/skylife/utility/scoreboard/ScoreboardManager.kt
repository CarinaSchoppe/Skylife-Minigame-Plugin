package com.carinaschoppe.skylife.utility.scoreboard

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.kit.KitManager
import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

/**
 * Manages the creation, updating, and removal of player scoreboards.
 */
object ScoreboardManager {

    private val playerScoreboards = mutableMapOf<Player, Scoreboard>()

    /**
     * Creates and assigns a new scoreboard to a player when they join a game.
     *
     * @param player The player to set the scoreboard for.
     * @param game The game the player has joined.
     */
    fun setScoreboard(player: Player, game: Game) {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective = scoreboard.registerNewObjective("skylife", Criteria.DUMMY, getAnimatedTitle())
        objective.displaySlot = DisplaySlot.SIDEBAR

        // Set initial scores for the compact layout
        objective.getScore("§7Map: §e${game.mapName}").score = 7
        objective.getScore(" ").score = 6
        objective.getScore("§7Players: §e${game.livingPlayers.size}/${game.maxPlayers}").score = 5
        objective.getScore("§7Kills: §e0").score = 4
        objective.getScore("§7Kit: §e-").score = 3
        objective.getScore("  ").score = 2
        objective.getScore("§7Rank: §e#-").score = 1

        playerScoreboards[player] = scoreboard
        player.scoreboard = scoreboard
        updateScoreboard(player, game)
    }

    /**
     * Updates the content of a player's scoreboard with the latest game information.
     *
     * @param player The player whose scoreboard needs updating.
     * @param game The game providing the stats.
     */
    fun updateScoreboard(player: Player, game: Game) {
        val scoreboard = player.scoreboard
        val objective = scoreboard.getObjective("skylife") ?: return

        // Clear old dynamic entries to prevent clutter
        scoreboard.entries.forEach { entry ->
            if (entry.startsWith("§7Players:") || entry.startsWith("§7Kills:") || entry.startsWith("§7Kit:") || entry.startsWith("§7Rank:")) {
                scoreboard.resetScores(entry)
            }
        }

        val rank = StatsUtility.getPlayerRank(player)
        val kit = KitManager.getSelectedKit(player)?.name ?: "None"

        // Update dynamic lines
        objective.getScore("§7Players: §e${game.livingPlayers.size}/${game.maxPlayers}").score = 5
        objective.getScore("§7Kills: §e${game.gameKills.getOrDefault(player.uniqueId, 0)}").score = 4
        objective.getScore("§7Kit: §e$kit").score = 3
        objective.getScore("§7Rank: §e#$rank").score = 1

        // Update title animation
        objective.displayName(getAnimatedTitle())
    }

    /**
     * Removes the custom scoreboard from a player, restoring the default server scoreboard.
     *
     * @param player The player whose scoreboard should be removed.
     */
    fun removeScoreboard(player: Player) {
        playerScoreboards.remove(player)
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }

    /**
     * Generates an animated, colored title component for the scoreboard.
     * The color cycles through a predefined list based on the current system time.
     *
     * @return A [Component] representing the animated title.
     */
    private fun getAnimatedTitle(): Component {
        val title = ConfigurationLoader.config.scoreboardTitle
        val colors = listOf(NamedTextColor.AQUA, NamedTextColor.BLUE, NamedTextColor.DARK_AQUA)
        val time = (System.currentTimeMillis() / 1000) % colors.size
        val color = colors[time.toInt()]
        // Use the Messages legacy serializer to handle '&' color codes from the config
        return Messages.legacy(title).color(color).decorate(TextDecoration.BOLD)
    }
}