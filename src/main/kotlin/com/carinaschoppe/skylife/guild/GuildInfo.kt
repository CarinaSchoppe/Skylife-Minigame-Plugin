package com.carinaschoppe.skylife.guild

import java.util.*

data class GuildInfo(
    val id: Int,
    val name: String,
    val tag: String,
    val leaderUUID: UUID,
    val members: MutableMap<UUID, GuildRole>,
    var friendlyFireEnabled: Boolean
)