package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.platform.PluginContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.persistence.PersistentDataType

/**
 * GUI for managing spawn locations of a game pattern.
 */
object SpawnManagementGui {

    private val KEY_SPAWN_INDEX: NamespacedKey by lazy { NamespacedKey(PluginContext.plugin, "spawn_index") }
    private val KEY_ACTION: NamespacedKey by lazy { NamespacedKey(PluginContext.plugin, "spawn_action") }

    /**
     * Opens the spawn management GUI.
     */
    fun openSpawnGUI(player: Player, gamePattern: GamePattern) {
        val holder = SpawnHolder(gamePattern)
        val inventory = Bukkit.createInventory(
            holder,
            54,
            Component.text("Spawns: ${gamePattern.mapName}", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
        )
        holder.setInventory(inventory)

        updateSpawnGUI(inventory, gamePattern, player)
        player.openInventory(inventory)
    }

    /**
     * Updates the spawn GUI with current spawn locations.
     */
    fun updateSpawnGUI(inventory: Inventory, gamePattern: GamePattern, player: Player) {
        inventory.clear()

        // Display spawns
        gamePattern.gameLocationManager.spawnLocations.forEachIndexed { index, spawn ->
            if (index < 45) {
                inventory.setItem(
                    index,
                    ItemBuilder(Material.END_PORTAL_FRAME)
                        .addName(Component.text("Spawn #${index + 1}", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        .addLore(
                            Component.text("World: ${spawn.world}", NamedTextColor.GRAY),
                            Component.text("X: ${"%.2f".format(spawn.x)}", NamedTextColor.GRAY),
                            Component.text("Y: ${"%.2f".format(spawn.y)}", NamedTextColor.GRAY),
                            Component.text("Z: ${"%.2f".format(spawn.z)}", NamedTextColor.GRAY),
                            Component.empty(),
                            Component.text("Left-Click: Teleport", NamedTextColor.GREEN),
                            Component.text("Right-Click: Delete", NamedTextColor.RED)
                        )
                        .modifyMeta { meta ->
                            meta.persistentDataContainer[KEY_SPAWN_INDEX, PersistentDataType.INTEGER] = index
                        }
                        .build()
                )
            }
        }

        // Add spawn button
        inventory.setItem(
            45,
            ItemBuilder(Material.LIME_CONCRETE)
                .addName(Component.text("+ Add Spawn", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(
                    Component.text("Click to add your current", NamedTextColor.GRAY),
                    Component.text("location as a spawn point", NamedTextColor.GRAY)
                )
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "add_spawn"
                }
                .build()
        )

        // Back button
        inventory.setItem(
            49,
            ItemBuilder(Material.ARROW)
                .addName(Component.text("← Back", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(Component.text("Return to game settings", NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "back"
                }
                .build()
        )

        // Info display
        inventory.setItem(
            53,
            ItemBuilder(Material.BOOK)
                .addName(Component.text("Info", NamedTextColor.AQUA, TextDecoration.BOLD))
                .addLore(
                    Component.text("Total Spawns: ${gamePattern.gameLocationManager.spawnLocations.size}", NamedTextColor.WHITE),
                    Component.text("Required: At least 2", NamedTextColor.GRAY)
                )
                .build()
        )
    }

    /**
     * Gets the spawn index from an item's PDC.
     */
    fun getSpawnIndex(item: org.bukkit.inventory.ItemStack?): Int? {
        val meta = item?.itemMeta ?: return null
        return meta.persistentDataContainer[KEY_SPAWN_INDEX, PersistentDataType.INTEGER]
    }

    /**
     * Gets the action from an item's PDC.
     */
    fun getAction(item: org.bukkit.inventory.ItemStack?): String? {
        val meta = item?.itemMeta ?: return null
        return meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING]
    }

    /**
     * Holder for the spawn inventory.
     */
    class SpawnHolder(val gamePattern: GamePattern) : InventoryHolder {
        private lateinit var inv: Inventory

        fun setInventory(inventory: Inventory) {
            this.inv = inventory
        }

        override fun getInventory(): Inventory = inv
    }
}
