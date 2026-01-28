package com.carinaschoppe.skylife.utility.scoreboard

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.guild.GuildManager
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

/**
 * Manages the creation, updating, and removal of player scoreboards.
 */
object ScoreboardManager {

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

        // Clear existing entries to keep the layout clean and avoid duplicates.
        scoreboard.entries.forEach { entry -> scoreboard.resetScores(entry) }

        val lines = buildScoreboardLines(player, game)
        val maxScore = lines.size

        lines.forEachIndexed { index, line ->
            objective.getScore(line).score = maxScore - index
        }

        // Update title animation
        objective.displayName(getAnimatedTitle())
    }

    /**
     * Removes the custom scoreboard from a player, restoring the default server scoreboard.
     *
     * @param player The player whose scoreboard should be removed.
     */
    fun removeScoreboard(player: Player) {
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }

    /**
     * Generates an animated, colored title component for the scoreboard.
     * The color cycles through a predefined list based on the current system time.
     *
     * @return A [Component] representing the animated title.
     */
    private fun getAnimatedTitle(): Component {
        val scoreboardConfig = ConfigurationLoader.config.scoreboard
        val title = scoreboardConfig.title.takeIf { it.isNotBlank() } ?: ConfigurationLoader.config.scoreboardTitle
        val replacedTitle = ScoreboardTextRenderer.applyPlaceholders(title, mapOf("{server}" to scoreboardConfig.serverName))
        val hasMiniMessageTags = replacedTitle.contains('<') && replacedTitle.contains('>')
        val baseComponent = if (hasMiniMessageTags) {
            Messages.parse(replacedTitle)
        } else {
            Messages.parse(replacedTitle).decorate(TextDecoration.BOLD)
        }

        if (!scoreboardConfig.animateTitle) {
            return baseComponent
        }

        if (hasMiniMessageTags) {
            return baseComponent
        }

        val time = (System.currentTimeMillis() / 1000) % titleColors.size
        return baseComponent.color(titleColors[time.toInt()])
    }

    private fun buildScoreboardLines(player: Player, game: Game): List<String> {
        val scoreboardConfig = ConfigurationLoader.config.scoreboard
        val rank = StatsUtility.getPlayerRank(player)
        val guild = GuildManager.getPlayerGuild(player.uniqueId)
        val guildName = guild?.name ?: "None"
        val guildTag = guild?.tag ?: ""

        val placeholders = mapOf(
            "{server}" to scoreboardConfig.serverName,
            "{map}" to game.mapName,
            "{alive}" to game.livingPlayers.size.toString(),
            "{max}" to game.maxPlayers.toString(),
            "{kills}" to game.gameKills.getOrDefault(player.uniqueId, 0).toString(),
            "{kills_total}" to game.gameKills.values.sum().toString(),
            "{rank}" to rank.toString(),
            "{player}" to player.name,
            "{state}" to game.state.name,
            "{guild}" to guildName,
            "{guild_tag}" to guildTag
        )

        val templateLines = scoreboardConfig.lines.ifEmpty { defaultLines }
        return ScoreboardTextRenderer.renderLines(templateLines, placeholders, MAX_LINES)
    }

    private val titleColors = listOf(NamedTextColor.AQUA, NamedTextColor.BLUE, NamedTextColor.DARK_AQUA)
    private val defaultLines = listOf(
        "<dark_gray><strikethrough>----------------</strikethrough></dark_gray>",
        "<aqua>Server</aqua><gray>: </gray><white>{server}</white>",
        "<aqua>Map</aqua><gray>: </gray><white>{map}</white>",
        "<aqua>Alive</aqua><gray>: </gray><green>{alive}</green><gray>/</gray><green>{max}</green>",
        "<aqua>Kills</aqua><gray>: </gray><red>{kills}</red>",
        "<aqua>Guild</aqua><gray>: </gray><light_purple>{guild}</light_purple>",
        "<aqua>Rank</aqua><gray>: </gray><gold>#{rank}</gold>",
        "<dark_gray><strikethrough>----------------</strikethrough></dark_gray>"
    )
    private const val MAX_LINES = 15
}
