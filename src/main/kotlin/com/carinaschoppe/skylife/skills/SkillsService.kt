package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.skills.persistence.PlayerSkillSelectionRepository
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SkillsService(
    private val repository: PlayerSkillSelectionRepository,
    private val configProvider: SkillsConfigProvider,
    private val rankProvider: PlayerRankProvider,
    private val unlockService: SkillUnlockService
) {

    // Cache: Player UUID -> Set of selected skills
    private val selectedSkills = ConcurrentHashMap<UUID, MutableSet<Skill>>()

    // Cache: Player UUID -> Set of active skills (currently in use in-game)
    private val activeSkills = ConcurrentHashMap<UUID, MutableSet<Skill>>()

    /**
     * Gets the maximum number of skills a player can select based on their rank.
     * Values are loaded from config.json (default: USER=2, VIP=3, VIP+=4)
     * Staff ranks (ADMIN, DEV, MOD) and VIP+ get VIP+ benefits (4 skill slots).
     */
    fun getMaxSkills(player: Player): Int {
        val config = configProvider.maxSkillsConfig()
        val rank = rankProvider.getRank(player)
        return when (rank) {
            com.carinaschoppe.skylife.economy.PlayerRank.ADMIN,
            com.carinaschoppe.skylife.economy.PlayerRank.DEV,
            com.carinaschoppe.skylife.economy.PlayerRank.MOD,
            com.carinaschoppe.skylife.economy.PlayerRank.VIP_PLUS -> config.vipPlus

            com.carinaschoppe.skylife.economy.PlayerRank.VIP -> config.vip
            com.carinaschoppe.skylife.economy.PlayerRank.USER -> config.default
        }
    }

    /**
     * Loads all player skill selections from database into cache.
     * Should be called on plugin startup.
     */
    fun loadSkills() {
        repository.loadAllSelections().forEach { (uuid, skills) ->
            if (skills.isNotEmpty()) {
                selectedSkills[uuid] = skills.toMutableSet()
            }
        }
    }

    /**
     * Gets the selected skills for a player.
     * @param player The player
     * @return Set of selected skills (may be empty)
     */
    fun getSelectedSkills(player: Player): Set<Skill> {
        return selectedSkills[player.uniqueId]?.toSet() ?: emptySet()
    }

    /**
     * Gets the selected skills for a player by UUID.
     * @param uuid The player UUID
     * @return Set of selected skills (may be empty)
     */
    fun getSelectedSkills(uuid: UUID): Set<Skill> {
        return selectedSkills[uuid]?.toSet() ?: emptySet()
    }

    /**
     * Checks if a player has a specific skill selected.
     * @param player The player
     * @param skill The skill to check
     * @return true if the skill is selected
     */
    fun hasSkillSelected(player: Player, skill: Skill): Boolean {
        return selectedSkills[player.uniqueId]?.contains(skill) ?: false
    }

    /**
     * Toggles a skill selection for a player.
     * @param player The player
     * @param skill The skill to toggle
     * @return Result with true if selected, false if unselected, or error message
     */
    fun toggleSkill(player: Player, skill: Skill): Result<Boolean> {
        val uuid = player.uniqueId
        val skills = selectedSkills.getOrPut(uuid) { mutableSetOf() }

        return if (skills.contains(skill)) {
            // Unselect skill
            skills.remove(skill)
            saveSkillSelection(uuid, skills)
            Result.success(false)
        } else {
            // Check if skill is unlocked
            if (!unlockService.hasUnlocked(uuid, skill)) {
                return Result.failure(Exception("You must unlock this skill before you can select it!"))
            }

            // Check if player can select more skills
            val maxSkills = getMaxSkills(player)
            if (skills.size >= maxSkills) {
                return Result.failure(Exception("You already have $maxSkills skills selected. Unselect one first."))
            }

            // Select skill
            skills.add(skill)
            saveSkillSelection(uuid, skills)
            Result.success(true)
        }
    }

    /**
     * Activates selected skills for a player (when game starts).
     * @param player The player
     */
    fun activateSkills(player: Player) {
        val skills = selectedSkills[player.uniqueId] ?: return
        activeSkills[player.uniqueId] = skills.toMutableSet()
    }

    /**
     * Deactivates all skills for a player (when game ends).
     * @param player The player
     */
    fun deactivateSkills(player: Player) {
        activeSkills.remove(player.uniqueId)
    }

    /**
     * Gets the currently active skills for a player (in-game).
     * @param player The player
     * @return Set of active skills (may be empty)
     */
    fun getActiveSkills(player: Player): Set<Skill> {
        return activeSkills[player.uniqueId]?.toSet() ?: emptySet()
    }

    /**
     * Checks if a player has a specific skill active.
     * @param player The player
     * @param skill The skill to check
     * @return true if the skill is active
     */
    fun hasSkillActive(player: Player, skill: Skill): Boolean {
        return activeSkills[player.uniqueId]?.contains(skill) ?: false
    }

    /**
     * Clears skill selection for a player (admin command).
     * @param uuid The player UUID
     */
    fun clearSkills(uuid: UUID) {
        selectedSkills.remove(uuid)
        activeSkills.remove(uuid)
        repository.deleteSelection(uuid)
    }

    private fun saveSkillSelection(uuid: UUID, skills: Set<Skill>) {
        repository.saveSelection(uuid, skills)
    }
}
