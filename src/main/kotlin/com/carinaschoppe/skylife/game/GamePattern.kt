package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.game.managers.GameLocationManager


class GamePattern(val mapName: String) {


    var minPlayers: Int = 0
    var maxPlayers: Int = 0

    val gameLocationManager = GameLocationManager()


}
