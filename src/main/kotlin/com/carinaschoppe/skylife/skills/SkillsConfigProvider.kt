package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.utility.configuration.MaxSkillsConfig

interface SkillsConfigProvider {
    fun maxSkillsConfig(): MaxSkillsConfig
}