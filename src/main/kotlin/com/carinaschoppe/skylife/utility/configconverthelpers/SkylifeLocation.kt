package com.carinaschoppe.skylife.utility.configconverthelpers

/**
 * A data class representing a location in a Minecraft world with all necessary coordinates and orientation.
 *
 * This class is designed to be serializable and is commonly used for saving and loading
 * locations to/from configuration files. It includes both positional (x, y, z) and
 * rotational (yaw, pitch) components, along with the world name.
 *
 * @property world The name of the world this location is in
 * @property x The x-coordinate of the location
 * @property y The y-coordinate of the location
 * @property z The z-coordinate of the location
 * @property yaw The horizontal rotation (in degrees), where 0 is south, 90 is west, 180 is north, and 270 is east
 * @property pitch The vertical rotation (in degrees), where -90 is straight up, 0 is level, and 90 is straight down
 */
data class SkylifeLocation(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
)
