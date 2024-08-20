package com.carinaschoppe.skylife.game.management


class GamePattern {

    lateinit var mapName: String

    var minPlayers: Int = 0
    var maxPlayerCount: Int = 0

    val gameLocationManagement = GameLocationManagement()

}
