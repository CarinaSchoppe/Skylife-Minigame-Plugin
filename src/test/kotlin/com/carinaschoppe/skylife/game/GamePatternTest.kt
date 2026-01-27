package com.carinaschoppe.skylife.game

import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import org.junit.jupiter.api.Test

class GamePatternTest {

    @Test
    fun `isComplete returns false when locations missing`() {
        val pattern = GamePattern("Map")
        pattern.minPlayers = 1
        pattern.maxPlayers = 4

        kotlin.test.assertFalse(pattern.isComplete())
    }

    @Test
    fun `isComplete returns true when all fields set`() {
        val pattern = GamePattern("Map")
        pattern.minPlayers = 1
        pattern.maxPlayers = 4

        val location = SkylifeLocation("world", 0.0, 64.0, 0.0, 0f, 0f)
        pattern.gameLocationManager.lobbyLocation = location
        pattern.gameLocationManager.spectatorLocation = location
        pattern.gameLocationManager.mainLocation = location
        pattern.gameLocationManager.spawnLocations.add(location)

        kotlin.test.assertTrue(pattern.isComplete())
    }
}
