package com.carinaschoppe.skylife.utility.scoreboard

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
 * Manages the lobby scoreboard shown to players when they're not in a game.
 */
object LobbyScoreboardManager {

    /**
     * Creates and assigns a lobby scoreboard to a player.
     * @param player The player to set the scoreboard for
     */
    fun setLobbyScoreboard(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective = scoreboard.registerNewObjective("skylife_lobby", Criteria.DUMMY, getAnimatedTitle())
        objective.displaySlot = DisplaySlot.SIDEBAR

        player.scoreboard = scoreboard
        updateLobbyScoreboard(player)
    }

    /**
     * Updates the lobby scoreboard with current player information.
     * @param player The player whose scoreboard needs updating
     */
    fun updateLobbyScoreboard(player: Player) {
        val scoreboard = player.scoreboard
        val objective = scoreboard.getObjective("skylife_lobby") ?: return

        // Clear existing entries
        scoreboard.entries.forEach { entry -> scoreboard.resetScores(entry) }

        val lines = buildLobbyScoreboardLines(player)
        val maxScore = lines.size

        lines.forEachIndexed { index, line ->
            objective.getScore(line).score = maxScore - index
        }

        // Update title animation
        objective.displayName(getAnimatedTitle())
    }

    /**
     * Removes the lobby scoreboard from a player.
     * @param player The player whose scoreboard should be removed
     */
    fun removeLobbyScoreboard(player: Player) {
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }

    /**
     * Generates an animated title for the lobby scoreboard.
     * @return A [Component] representing the animated title
     */
    private fun getAnimatedTitle(): Component {
        val scoreboardConfig = ConfigurationLoader.config.scoreboard
        val title = scoreboardConfig.lobbyTitle.takeIf { it.isNotBlank() } ?: scoreboardConfig.title.takeIf { it.isNotBlank() } ?: "SKYLIFE"
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

    private fun buildLobbyScoreboardLines(player: Player): List<String> {
        val scoreboardConfig = ConfigurationLoader.config.scoreboard
        val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(player)
        val stats = StatsUtility.statsPlayers.firstOrNull { it.uuid == player.uniqueId.toString() }

        // Get selected skills for display
        val selectedSkills = com.carinaschoppe.skylife.skills.SkillsManager.getSelectedSkills(player).toList()
        val skill1Name = selectedSkills.getOrNull(0)?.displayName ?: "None"
        val skill2Name = selectedSkills.getOrNull(1)?.displayName ?: "None"

        // Get player coins
        val coins = com.carinaschoppe.skylife.economy.CoinManager.getCoins(player.uniqueId)

        // Get player guild
        val guild = GuildManager.getPlayerGuild(player.uniqueId)
        val guildName = guild?.name ?: "None"

        val placeholders = mapOf(
            "{server}" to scoreboardConfig.serverName,
            "{rank}" to rank.displayName,
            "{rank_color}" to getRankColorMiniMessage(rank),
            "{player}" to player.name,
            "{online}" to Bukkit.getOnlinePlayers().size.toString(),
            "{max_players}" to Bukkit.getMaxPlayers().toString(),
            "{kills}" to (stats?.kills?.toString() ?: "0"),
            "{deaths}" to (stats?.deaths?.toString() ?: "0"),
            "{wins}" to (stats?.wins?.toString() ?: "0"),
            "{games}" to (stats?.games?.toString() ?: "0"),
            "{points}" to (stats?.points?.toString() ?: "0"),
            "{player_rank}" to StatsUtility.getPlayerRank(player).toString(),
            "{skill1}" to skill1Name,
            "{skill2}" to skill2Name,
            "{coins}" to coins.toString(),
            "{guild}" to guildName
        )

        val templateLines = scoreboardConfig.lobbyLines.ifEmpty { defaultLobbyLines }
        return ScoreboardTextRenderer.renderLines(templateLines, placeholders, MAX_LINES)
    }

    /**
     * Converts a PlayerRank's color to MiniMessage format.
     * @param rank The player rank
     * @return MiniMessage color tag (e.g., "<red>", "<aqua>")
     */
    private fun getRankColorMiniMessage(rank: com.carinaschoppe.skylife.economy.PlayerRank): String {
        return when (rank) {
            com.carinaschoppe.skylife.economy.PlayerRank.USER -> "<gray>"
            com.carinaschoppe.skylife.economy.PlayerRank.VIP -> MM_GREEN
            com.carinaschoppe.skylife.economy.PlayerRank.VIP_PLUS -> MM_AQUA
            com.carinaschoppe.skylife.economy.PlayerRank.MOD -> MM_GREEN
            com.carinaschoppe.skylife.economy.PlayerRank.DEV -> MM_AQUA
            com.carinaschoppe.skylife.economy.PlayerRank.ADMIN -> "<red>"
        }
    }

    private val titleColors = listOf(NamedTextColor.AQUA, NamedTextColor.BLUE, NamedTextColor.DARK_AQUA)
    private val defaultLobbyLines = listOf(
        DIVIDER_LINE,
        "<aqua>Server</aqua><gray>: </gray><white>{server}</white>",
        "",
        "<aqua>Rank</aqua><gray>: </gray>{rank_color}{rank}",
        "<aqua>Guild</aqua><gray>: </gray><light_purple>{guild}</light_purple>",
        "<aqua>Stats Rank</aqua><gray>: </gray><gold>#{player_rank}</gold>",
        "<aqua>Coins</aqua><gray>: </gray><gold>{coins}</gold>",
        "",
        "<aqua>Kills</aqua><gray>: </gray><red>{kills}</red>",
        "<aqua>Wins</aqua><gray>: </gray><yellow>{wins}</yellow>",
        "<aqua>Games</aqua><gray>: </gray><white>{games}</white>",
        DIVIDER_LINE
    )
    private const val MAX_LINES = 15
    private const val MM_GREEN = "<green>"
    private const val MM_AQUA = "<aqua>"
    private const val DIVIDER_LINE = "<dark_gray><strikethrough>----------------</strikethrough></dark_gray>"
}
