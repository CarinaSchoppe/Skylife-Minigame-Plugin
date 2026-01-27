package com.carinaschoppe.skylife.skills

import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Database table for player skill selections.
 */
object PlayerSkills : IntIdTable() {
    val playerUUID = varchar("player_uuid", 36).uniqueIndex()
    val skill1 = enumerationByName("skill1", 30, Skill::class).nullable()
    val skill2 = enumerationByName("skill2", 30, Skill::class).nullable()
}

/**
 * Entity class for player skills database operations.
 */
class PlayerSkillSelection(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, PlayerSkillSelection>(PlayerSkills)

    var playerUUID by PlayerSkills.playerUUID
    var skill1 by PlayerSkills.skill1
    var skill2 by PlayerSkills.skill2
}

/**
 * Manages player skill selections and activation.
 */
object SkillsManager {

    // Cache: Player UUID -> Set of selected skills
    private val selectedSkills = ConcurrentHashMap<UUID, MutableSet<Skill>>()

    // Cache: Player UUID -> Set of active skills (currently in use in-game)
    private val activeSkills = ConcurrentHashMap<UUID, MutableSet<Skill>>()

    /**
     * Maximum number of skills a player can select.
     */
    const val MAX_SKILLS = 2

    /**
     * Loads all player skill selections from database into cache.
     * Should be called on plugin startup.
     */
    fun loadSkills() {
        transaction {
            PlayerSkillSelection.all().forEach { selection ->
                val uuid = UUID.fromString(selection.playerUUID)
                val skills = mutableSetOf<Skill>()

                selection.skill1?.let { skills.add(it) }
                selection.skill2?.let { skills.add(it) }

                if (skills.isNotEmpty()) {
                    selectedSkills[uuid] = skills
                }
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

        if (skills.contains(skill)) {
            // Unselect skill
            skills.remove(skill)
            saveSkillSelection(uuid, skills)
            return Result.success(false)
        } else {
            // Check if player can select more skills
            if (skills.size >= MAX_SKILLS) {
                return Result.failure(Exception("You already have $MAX_SKILLS skills selected. Unselect one first."))
            }

            // Select skill
            skills.add(skill)
            saveSkillSelection(uuid, skills)
            return Result.success(true)
        }
    }

    /**
     * Saves skill selection to database.
     */
    private fun saveSkillSelection(uuid: UUID, skills: Set<Skill>) {
        transaction {
            val existing = PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()

            val skillsList = skills.toList()
            val skill1 = skillsList.getOrNull(0)
            val skill2 = skillsList.getOrNull(1)

            if (existing != null) {
                existing.skill1 = skill1
                existing.skill2 = skill2
            } else {
                PlayerSkillSelection.new {
                    this.playerUUID = uuid.toString()
                    this.skill1 = skill1
                    this.skill2 = skill2
                }
            }
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

        transaction {
            PlayerSkillSelection.find { PlayerSkills.playerUUID eq uuid.toString() }.firstOrNull()?.delete()
        }
    }
}
