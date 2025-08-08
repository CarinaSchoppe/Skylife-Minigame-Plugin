package com.carinaschoppe.skylife.utility.ui.inventoryholders

/**
 * A specialized [InventoryHolderFactory] for creating game overview GUIs.
 *
 * This factory creates a 4-row (36-slot) inventory with a title of "Game Overview".
 * It's designed to be used as a base for displaying game-related information
 * and controls to players, such as available games, game status, or other
 * game management options.
 *
 * The actual content of the inventory should be populated by the code that
 * uses this factory, typically by adding items to the inventory after creation.
 *
 * @see InventoryHolderFactory
 */
class GameOverviewHolderFactory : InventoryHolderFactory("Game Overview", 36)