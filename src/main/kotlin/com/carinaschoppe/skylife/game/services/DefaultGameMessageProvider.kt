package com.carinaschoppe.skylife.game.services

import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component

class DefaultGameMessageProvider : GameMessageProvider {
    override fun gameFullOrStarted(): Component = Messages.ERROR_GAME_FULL_OR_STARTED
    override fun priorityJoinFull(): Component = Messages.PRIORITY_JOIN_FULL
    override fun priorityJoinKicked(kickerName: String): Component = Messages.PRIORITY_JOIN_KICKED(kickerName)
}