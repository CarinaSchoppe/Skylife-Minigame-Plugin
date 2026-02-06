package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.game.Game
import com.carinaschoppe.skylife.game.gamestates.GameState
import com.carinaschoppe.skylife.game.gamestates.IngameState
import com.carinaschoppe.skylife.skills.SkillEffectsManager
import com.carinaschoppe.skylife.skills.SkillsManager
import com.carinaschoppe.skylife.utility.PlayerPriority
import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.configuration.PriorityJoinConfig
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsUtility
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface GameConfigProvider {
    fun priorityJoinConfig(): PriorityJoinConfig
}

class DefaultGameConfigProvider : GameConfigProvider {
    override fun priorityJoinConfig(): PriorityJoinConfig = ConfigurationLoader.config.priorityJoin
}

interface PlayerPriorityResolver {
    fun getPlayerPriority(player: Player): PlayerPriority
    fun findPlayerToKick(players: List<Player>, joiningPlayer: Player): Player?
}

class DefaultPlayerPriorityResolver : PlayerPriorityResolver {
    override fun getPlayerPriority(player: Player): PlayerPriority {
        return PlayerPriority.getPlayerPriority(player)
    }

    override fun findPlayerToKick(players: List<Player>, joiningPlayer: Player): Player? {
        return PlayerPriority.findPlayerToKick(players, joiningPlayer)
    }
}

interface GameMessageProvider {
    fun gameFullOrStarted(): Component
    fun priorityJoinFull(): Component
    fun priorityJoinKicked(kickerName: String): Component
}

class DefaultGameMessageProvider : GameMessageProvider {
    override fun gameFullOrStarted(): Component = Messages.ERROR_GAME_FULL_OR_STARTED
    override fun priorityJoinFull(): Component = Messages.PRIORITY_JOIN_FULL
    override fun priorityJoinKicked(kickerName: String): Component = Messages.PRIORITY_JOIN_KICKED(kickerName)
}

interface StatsService {
    fun addStatsToPlayerWhenJoiningGame(player: Player)
}

class DefaultStatsService : StatsService {
    override fun addStatsToPlayerWhenJoiningGame(player: Player) {
        StatsUtility.addStatsToPlayerWhenJoiningGame(player)
    }
}

interface SkillLifecycleService {
    fun deactivateSkills(player: Player)
    fun removeSkillEffects(player: Player)
}

class DefaultSkillLifecycleService : SkillLifecycleService {
    override fun deactivateSkills(player: Player) {
        SkillsManager.deactivateSkills(player)
    }

    override fun removeSkillEffects(player: Player) {
        SkillEffectsManager.removeSkillEffects(player)
    }
}

interface GameStateFactory {
    fun createIngameState(game: Game): GameState<Player>
}

class DefaultGameStateFactory : GameStateFactory {
    override fun createIngameState(game: Game): GameState<Player> {
        return IngameState(game)
    }
}
