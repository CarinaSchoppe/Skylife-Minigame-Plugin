package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.configuration.MaxSkillsConfig

class DefaultSkillsConfigProvider : SkillsConfigProvider {
    override fun maxSkillsConfig(): MaxSkillsConfig = ConfigurationLoader.config.maxSkills
}

