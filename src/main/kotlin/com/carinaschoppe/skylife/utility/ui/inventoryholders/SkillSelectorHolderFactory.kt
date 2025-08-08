package com.carinaschoppe.skylife.utility.ui.inventoryholders

/**
 * A specialized [InventoryHolderFactory] for creating skill selection GUIs.
 *
 * This factory creates a single-row (9-slot) inventory with a title of "Skill Overview".
 * It's designed to be used as a base for displaying available skills that players
 * can select or interact with, such as special abilities or power-ups.
 *
 * The actual skill items should be added to the inventory by the code that uses
 * this factory, typically by adding items that represent different skills.
 *
 * @see InventoryHolderFactory
 */
class SkillSelectorHolderFactory : InventoryHolderFactory("Skill Overview", 9)
