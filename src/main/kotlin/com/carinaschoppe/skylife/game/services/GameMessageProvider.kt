package com.carinaschoppe.skylife.game.services

import net.kyori.adventure.text.Component

interface GameMessageProvider {
    fun gameFullOrStarted(): Component
    fun priorityJoinFull(): Component
    fun priorityJoinKicked(kickerName: String): Component
}