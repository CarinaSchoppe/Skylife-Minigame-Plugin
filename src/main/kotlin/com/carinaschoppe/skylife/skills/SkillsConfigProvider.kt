package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.utility.configuration.MaxSkillsConfig

fun interface SkillsConfigProvider {
    fun maxSkillsConfig(): MaxSkillsConfig
}