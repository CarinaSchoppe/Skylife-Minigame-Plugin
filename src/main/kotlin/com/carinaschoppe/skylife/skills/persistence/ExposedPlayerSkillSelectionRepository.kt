package com.carinaschoppe.skylife.skills.persistence

import com.carinaschoppe.skylife.skills.Skill
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class ExposedPlayerSkillSelectionRepository : PlayerSkillSelectionRepository {

    override fun loadAllSelections(): Map<UUID, Set<Skill>> {
        val selections = mutableMapOf<UUID, Set<Skill>>()
        transaction {
            PlayerSkillSelection.all().forEach { selection ->
                val uuid = UUID.fromString(selection.playerUUID)
                val skills = mutableSetOf<Skill>()

                selection.skill1?.let { skills.add(it) }
                selection.skill2?.let { skills.add(it) }
                selection.skill3?.let { skills.add(it) }
                selection.skill4?.let { skills.add(it) }

                if (skills.isNotEmpty()) {
                    selections[uuid] = skills
                }
            }
        }
        return selections
    }

    override fun saveSelection(uuid: UUID, skills: Set<Skill>) {
        transaction {
            val existing = PlayerSkillSelection.find {
                PlayerSkillsSchema.playerUUID eq uuid.toString()
            }.firstOrNull()

            val skillsList = skills.toList()
            val skill1 = skillsList.getOrNull(0)
            val skill2 = skillsList.getOrNull(1)
            val skill3 = skillsList.getOrNull(2)
            val skill4 = skillsList.getOrNull(3)

            if (existing != null) {
                existing.skill1 = skill1
                existing.skill2 = skill2
                existing.skill3 = skill3
                existing.skill4 = skill4
            } else {
                PlayerSkillSelection.new {
                    this.playerUUID = uuid.toString()
                    this.skill1 = skill1
                    this.skill2 = skill2
                    this.skill3 = skill3
                    this.skill4 = skill4
                }
            }
        }
    }

    override fun deleteSelection(uuid: UUID) {
        transaction {
            PlayerSkillSelection.find {
                PlayerSkillsSchema.playerUUID eq uuid.toString()
            }.firstOrNull()?.delete()
        }
    }
}