package com.carinaschoppe.skylife.game

import org.bukkit.World

val Game.world: World
    get() = gameWorld ?: lobbyLocation.world
