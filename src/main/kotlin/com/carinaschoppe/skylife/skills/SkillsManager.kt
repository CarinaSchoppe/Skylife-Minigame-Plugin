package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.skills.persistence.ExposedPlayerSkillSelectionRepository
import com.carinaschoppe.skylife.skills.persistence.PlayerSkillSelectionRepository
import org.bukkit.entity.Player
import java.util.*

/**
 * Facade for skill operations. Delegates to [SkillsService] so the
 * persistence layer can be swapped for testing or future storage changes.
 */
object SkillsManager {

    @Volatile
    private var service: SkillsService = SkillsService(
        ExposedPlayerSkillSelectionRepository(),
        DefaultSkillsConfigProvider(),
        DefaultPlayerRankProvider(),
        DefaultSkillUnlockService()
    )

    fun initialize(
        repository: PlayerSkillSelectionRepository = ExposedPlayerSkillSelectionRepository(),
        configProvider: SkillsConfigProvider = DefaultSkillsConfigProvider(),
        rankProvider: PlayerRankProvider = DefaultPlayerRankProvider(),
        unlockService: SkillUnlockService = DefaultSkillUnlockService()
    ) {
        service = SkillsService(repository, configProvider, rankProvider, unlockService)
    }

    fun getMaxSkills(player: Player): Int = service.getMaxSkills(player)

    fun loadSkills() {
        service.loadSkills()
    }

    fun getSelectedSkills(player: Player): Set<Skill> = service.getSelectedSkills(player)

    fun getSelectedSkills(uuid: UUID): Set<Skill> = service.getSelectedSkills(uuid)

    fun hasSkillSelected(player: Player, skill: Skill): Boolean = service.hasSkillSelected(player, skill)

    fun toggleSkill(player: Player, skill: Skill): Result<Boolean> = service.toggleSkill(player, skill)

    fun activateSkills(player: Player) {
        service.activateSkills(player)
    }

    fun deactivateSkills(player: Player) {
        service.deactivateSkills(player)
    }

    fun getActiveSkills(player: Player): Set<Skill> = service.getActiveSkills(player)

    fun hasSkillActive(player: Player, skill: Skill): Boolean = service.hasSkillActive(player, skill)

    fun clearSkills(uuid: UUID) {
        service.clearSkills(uuid)
    }
}
