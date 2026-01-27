package com.carinaschoppe.skylife.game.managers

import com.carinaschoppe.skylife.utility.configconverthelpers.SkylifeLocation
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit

class GameLocationManagerTest {

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `convert location to skylife location`() {
        val world = MockBukkit.getMock()!!.addSimpleWorld("world")
        val location = Location(world, 1.0, 2.0, 3.0, 90f, 45f)

        val result = GameLocationManager.locationToSkylifeLocationConverter(location)

        kotlin.test.assertEquals("world", result.world)
        kotlin.test.assertEquals(1.0, result.x)
        kotlin.test.assertEquals(2.0, result.y)
        kotlin.test.assertEquals(3.0, result.z)
        kotlin.test.assertEquals(90f, result.yaw)
        kotlin.test.assertEquals(45f, result.pitch)
    }

    @Test
    fun `convert skylife location to location`() {
        val world = MockBukkit.getMock()!!.addSimpleWorld("world")
        val skylife = SkylifeLocation("world", 4.0, 5.0, 6.0, 0f, 0f)

        val location = GameLocationManager.skylifeLocationToLocationConverter(skylife)

        kotlin.test.assertEquals(world, location.world)
        kotlin.test.assertEquals(4.0, location.x)
        kotlin.test.assertEquals(5.0, location.y)
        kotlin.test.assertEquals(6.0, location.z)
    }
}
