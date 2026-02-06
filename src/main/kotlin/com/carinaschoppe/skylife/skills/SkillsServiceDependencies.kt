package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.economy.PlayerRank
import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.configuration.MaxSkillsConfig
import org.bukkit.entity.Player
import java.util.*

interface SkillsConfigProvider {
    fun maxSkillsConfig(): MaxSkillsConfig
}

class DefaultSkillsConfigProvider : SkillsConfigProvider {
    override fun maxSkillsConfig(): MaxSkillsConfig = ConfigurationLoader.config.maxSkills
}

interface PlayerRankProvider {
    fun getRank(player: Player): PlayerRank
}

class DefaultPlayerRankProvider : PlayerRankProvider {
    override fun getRank(player: Player): PlayerRank = PlayerRank.getRank(player)
}

interface SkillUnlockService {
    fun hasUnlocked(player: UUID, skill: Skill): Boolean
}

class DefaultSkillUnlockService : SkillUnlockService {
    override fun hasUnlocked(player: UUID, skill: Skill): Boolean {
        return SkillUnlockManager.hasUnlocked(player, skill)
    }
}
