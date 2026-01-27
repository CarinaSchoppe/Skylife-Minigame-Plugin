package com.carinaschoppe.skylife.testutil

import com.carinaschoppe.skylife.game.GameCluster

object GameClusterTestHelper {

    fun reset() {
        val clazz = GameCluster::class.java
        listOf("lobbyGames", "activeGames").forEach { fieldName ->
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val list = field.get(GameCluster) as MutableList<Any>
            list.clear()
        }
        GameCluster.gamePatterns.clear()
    }
}
