package com.carinaschoppe.skylife.utility.configuration

class Configurations {

    companion object {
        lateinit var instance: Configurations

    }

    val LOBBY_TIMER = 60
    val INGAME_TIMER = 60 * 15
    val END_TIMER = 10
    val PROTECTION_TIMER = 10

}