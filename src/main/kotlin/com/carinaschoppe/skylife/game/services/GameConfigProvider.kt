package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.configuration.PriorityJoinConfig

fun interface GameConfigProvider {
    fun priorityJoinConfig(): PriorityJoinConfig
}

