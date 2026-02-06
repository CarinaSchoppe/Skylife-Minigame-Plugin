package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.configuration.ConfigurationLoader
import com.carinaschoppe.skylife.utility.configuration.PriorityJoinConfig

class DefaultGameConfigProvider : GameConfigProvider {
    override fun priorityJoinConfig(): PriorityJoinConfig = ConfigurationLoader.config.priorityJoin
}