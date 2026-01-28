package com.carinaschoppe.skylife.utility.ui.inventoryholders

import com.carinaschoppe.skylife.game.GamePattern
import org.bukkit.entity.Player

/**
 * Inventory holder for the game setup GUI.
 */
open class GameSetupHolderFactory(
    open val player: Player,
    open val gamePattern: GamePattern
) : InventoryHolderFactory("Game Setup: ${gamePattern.mapName}", 54)
