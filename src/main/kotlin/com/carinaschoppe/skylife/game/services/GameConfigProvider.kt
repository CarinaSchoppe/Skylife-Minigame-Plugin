package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.configuration.PriorityJoinConfig

interface GameConfigProvider {
    fun priorityJoinConfig(): PriorityJoinConfig
}

