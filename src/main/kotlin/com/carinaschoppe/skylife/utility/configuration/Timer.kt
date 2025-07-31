package com.carinaschoppe.skylife.utility.configuration

class Timer {

    companion object {
        lateinit var instance: Timer

    }

    val LOBBY_TIMER = 60
    val INGAME_TIMER = 60 * 15
    val END_TIMER = 10
    val PROTECTION_TIMER = 10

}