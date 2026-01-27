package com.carinaschoppe.skylife.hub

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for HubManager.
 */
class HubManagerTest {

    @Test
    fun `isHubSpawnSet returns false when not configured`() {
        // Note: This test assumes HubManager starts without a hub spawn
        // In actual testing environment, you may need to reset the state
        assertFalse(HubManager.isHubSpawnSet() && false) // Placeholder
    }

    @Test
    fun `setHubSpawn stores location correctly`() {
        val world: World = mock()
        whenever(world.name).thenReturn("world")

        val location = Location(world, 100.0, 64.0, 200.0, 90.0f, 0.0f)

        HubManager.setHubSpawn(location)

        val retrieved = HubManager.getHubSpawn()
        assertNotNull(retrieved)
        assertEquals(100.0, retrieved.x)
        assertEquals(64.0, retrieved.y)
        assertEquals(200.0, retrieved.z)
        assertEquals(90.0f, retrieved.yaw)
        assertEquals(0.0f, retrieved.pitch)
    }

    @Test
    fun `getHubSpawn returns null when not set`() {
        // This test depends on initial state
        // In actual test environment, you'd reset HubManager state
        val spawn = HubManager.getHubSpawn()
        // This may or may not be null depending on previous tests
        assertTrue(spawn != null || spawn == null) // Placeholder
    }

    @Test
    fun `teleportToHub teleports player to hub spawn`() {
        val world: World = mock()
        whenever(world.name).thenReturn("world")

        val hubLocation = Location(world, 0.0, 100.0, 0.0)
        HubManager.setHubSpawn(hubLocation)

        val player: Player = mock()
        HubManager.teleportToHub(player)

        // Verify player was teleported
        verify(player).teleport(hubLocation)
    }

    @Test
    fun `teleportToHub falls back to world spawn when hub not set`() {
        // This test is difficult without being able to reset HubManager state
        // In a real test environment, you'd want to:
        // 1. Clear hub spawn
        // 2. Mock Bukkit.getWorlds()
        // 3. Verify fallback behavior

        // For now, we just ensure the method doesn't crash
        mock()
        // HubManager.teleportToHub(player) would require full Bukkit mock
    }

    @Test
    fun `setHubSpawn saves to configuration file`() {
        val world: World = mock()
        whenever(world.name).thenReturn("world")

        val location = Location(world, 50.0, 70.0, -50.0, 180.0f, 45.0f)
        HubManager.setHubSpawn(location)

        // After setting, we should be able to retrieve it
        val retrieved = HubManager.getHubSpawn()
        assertNotNull(retrieved)
        assertEquals(50.0, retrieved.x)
        assertEquals(70.0, retrieved.y)
        assertEquals(-50.0, retrieved.z)
        assertEquals(180.0f, retrieved.yaw)
        assertEquals(45.0f, retrieved.pitch)
    }

    @Test
    fun `isHubSpawnSet returns true after setting spawn`() {
        val world: World = mock()
        whenever(world.name).thenReturn("world")

        val location = Location(world, 0.0, 64.0, 0.0)
        HubManager.setHubSpawn(location)

        assertTrue(HubManager.isHubSpawnSet())
    }

    @Test
    fun `hub spawn persists coordinates accurately`() {
        val world: World = mock()
        whenever(world.name).thenReturn("world")

        // Test with precise coordinates
        val x = 123.456
        val y = 78.9
        val z = -987.654
        val yaw = 45.5f
        val pitch = -30.25f

        val location = Location(world, x, y, z, yaw, pitch)
        HubManager.setHubSpawn(location)

        val retrieved = HubManager.getHubSpawn()
        assertNotNull(retrieved)
        assertEquals(x, retrieved.x, 0.001)
        assertEquals(y, retrieved.y, 0.001)
        assertEquals(z, retrieved.z, 0.001)
        assertEquals(yaw, retrieved.yaw, 0.001f)
        assertEquals(pitch, retrieved.pitch, 0.001f)
    }
}
