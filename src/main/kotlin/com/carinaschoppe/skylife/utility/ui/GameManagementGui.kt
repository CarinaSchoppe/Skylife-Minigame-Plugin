package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.game.GameCluster
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
 * GUI for managing existing game patterns.
 * Allows admins to view all games and edit their settings.
 */
object GameManagementGui {

    private const val LORE_CLICK_TO_DECREASE = "Click to decrease"
    private const val LORE_CLICK_TO_INCREASE = "Click to increase"
    private const val LORE_CLICK_TO_SET_PREFIX = "Click to set to your"
    private const val LORE_CURRENT_LOCATION = "current location"
    private const val NOT_SET_TEXT = "✗ Not set"
    private const val LOCATION_SET_TEXT = "✓ Location set!"
    private const val MINIMUM_ONE_TEXT = "(Minimum: 1)"

    private val KEY_GAME_NAME: NamespacedKey by lazy { NamespacedKey(PluginContext.plugin, "manage_game_name") }
    private val KEY_ACTION: NamespacedKey by lazy { NamespacedKey(PluginContext.plugin, "manage_game_action") }

    /**
     * Opens the game list GUI showing all available game patterns.
     */
    fun openListGUI(player: Player) {
        val holder = GameListHolder()
        val inventory = Bukkit.createInventory(holder, 54, Component.text("Game Management", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
        holder.setInventory(inventory)

        // Fill with game items
        GameCluster.gamePatterns.forEachIndexed { index, pattern ->
            if (index < 54) {
                val item = ItemBuilder(Material.MAP)
                    .addName(Component.text(pattern.mapName, NamedTextColor.AQUA, TextDecoration.BOLD))
                    .addLore(
                        Component.text("Min Players: ${pattern.minPlayers}", NamedTextColor.GRAY),
                        Component.text("Max Players: ${pattern.maxPlayers}", NamedTextColor.GRAY),
                        Component.text("Min to Start: ${pattern.minPlayersToStart}", NamedTextColor.YELLOW),
                        Component.empty(),
                        Component.text("Click to edit", NamedTextColor.GREEN)
                    )
                    .modifyMeta { meta ->
                        meta.persistentDataContainer[KEY_GAME_NAME, PersistentDataType.STRING] = pattern.mapName
                    }
                    .build()
                inventory.setItem(index, item)
            }
        }

        player.openInventory(inventory)
    }

    /**
     * Opens the edit GUI for a specific game pattern.
     */
    fun openEditGUI(player: Player, gamePattern: GamePattern) {
        val holder = GameEditHolder(gamePattern)
        val inventory = Bukkit.createInventory(
            holder,
            54,
            Component.text("Edit: ${gamePattern.mapName}", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
        )
        holder.setInventory(inventory)

        updateEditGUI(inventory, gamePattern)
        player.openInventory(inventory)
    }

    /**
     * Updates the edit GUI with current values.
     */
    fun updateEditGUI(inventory: Inventory, gamePattern: GamePattern) {
        setMinPlayersItems(inventory, gamePattern)
        setMaxPlayersItems(inventory, gamePattern)
        setMinPlayersToStartItems(inventory, gamePattern)
        setLocationItems(inventory, gamePattern)
        setSpawnLocationsItem(inventory, gamePattern)
        setActionButtons(inventory)
    }

    private fun setMinPlayersItems(inventory: Inventory, gamePattern: GamePattern) {
        inventory.setItem(
            9,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Min Players", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_DECREASE, NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "minPlayers-"
                }
                .build()
        )

        inventory.setItem(
            10,
            ItemBuilder(Material.PLAYER_HEAD)
                .addName(Component.text("Min Players", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current: ${gamePattern.minPlayers}", NamedTextColor.WHITE),
                    Component.text(MINIMUM_ONE_TEXT, NamedTextColor.GRAY)
                )
                .addAmount(maxOf(1, gamePattern.minPlayers))
                .build()
        )

        inventory.setItem(
            11,
            ItemBuilder(Material.LIME_WOOL)
                .addName(Component.text("Increase Min Players", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_INCREASE, NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "minPlayers+"
                }
                .build()
        )
    }

    private fun setMaxPlayersItems(inventory: Inventory, gamePattern: GamePattern) {
        inventory.setItem(
            18,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Max Players", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_DECREASE, NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "maxPlayers-"
                }
                .build()
        )

        inventory.setItem(
            19,
            ItemBuilder(Material.PLAYER_HEAD)
                .addName(Component.text("Max Players", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current: ${gamePattern.maxPlayers}", NamedTextColor.WHITE),
                    Component.text("(Must be ≥ Min Players)", NamedTextColor.GRAY)
                )
                .addAmount(maxOf(1, gamePattern.maxPlayers))
                .build()
        )

        inventory.setItem(
            20,
            ItemBuilder(Material.LIME_WOOL)
                .addName(Component.text("Increase Max Players", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_INCREASE, NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "maxPlayers+"
                }
                .build()
        )
    }

    private fun setMinPlayersToStartItems(inventory: Inventory, gamePattern: GamePattern) {
        inventory.setItem(
            27,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Min to Start", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_DECREASE, NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "minPlayersToStart-"
                }
                .build()
        )

        inventory.setItem(
            28,
            ItemBuilder(Material.CLOCK)
                .addName(Component.text("Min Players to Start", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current: ${gamePattern.minPlayersToStart}", NamedTextColor.WHITE),
                    Component.text("Players needed to start countdown", NamedTextColor.GRAY),
                    Component.text(MINIMUM_ONE_TEXT, NamedTextColor.GRAY)
                )
                .addAmount(maxOf(1, gamePattern.minPlayersToStart))
                .build()
        )

        inventory.setItem(
            29,
            ItemBuilder(Material.LIME_WOOL)
                .addName(Component.text("Increase Min to Start", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_INCREASE, NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "minPlayersToStart+"
                }
                .build()
        )
    }

    private fun setLocationItems(inventory: Inventory, gamePattern: GamePattern) {
        val locationManager = gamePattern.gameLocationManager
        val lobbySet = locationManager.isLocationInitialized("lobby")
        inventory.setItem(
            36,
            ItemBuilder(if (lobbySet) Material.LIME_CONCRETE else Material.RED_CONCRETE)
                .addName(Component.text("Lobby Location", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    if (lobbySet) Component.text(LOCATION_SET_TEXT, NamedTextColor.GREEN)
                    else Component.text(NOT_SET_TEXT, NamedTextColor.RED),
                    Component.empty(),
                    Component.text(LORE_CLICK_TO_SET_PREFIX, NamedTextColor.GRAY),
                    Component.text(LORE_CURRENT_LOCATION, NamedTextColor.GRAY)
                )
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "set_lobby"
                }
                .build()
        )

        val spectatorSet = locationManager.isLocationInitialized("spectator")
        inventory.setItem(
            37,
            ItemBuilder(if (spectatorSet) Material.LIME_CONCRETE else Material.RED_CONCRETE)
                .addName(Component.text("Spectator Location", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    if (spectatorSet) Component.text(LOCATION_SET_TEXT, NamedTextColor.GREEN)
                    else Component.text(NOT_SET_TEXT, NamedTextColor.RED),
                    Component.empty(),
                    Component.text(LORE_CLICK_TO_SET_PREFIX, NamedTextColor.GRAY),
                    Component.text(LORE_CURRENT_LOCATION, NamedTextColor.GRAY)
                )
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "set_spectator"
                }
                .build()
        )

        val mainSet = locationManager.isLocationInitialized("main")
        inventory.setItem(
            38,
            ItemBuilder(if (mainSet) Material.LIME_CONCRETE else Material.RED_CONCRETE)
                .addName(Component.text("Main Location", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    if (mainSet) Component.text(LOCATION_SET_TEXT, NamedTextColor.GREEN)
                    else Component.text(NOT_SET_TEXT, NamedTextColor.RED),
                    Component.empty(),
                    Component.text(LORE_CLICK_TO_SET_PREFIX, NamedTextColor.GRAY),
                    Component.text(LORE_CURRENT_LOCATION, NamedTextColor.GRAY)
                )
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "set_main"
                }
                .build()
        )
    }

    private fun setSpawnLocationsItem(inventory: Inventory, gamePattern: GamePattern) {
        inventory.setItem(
            39,
            ItemBuilder(Material.END_PORTAL_FRAME)
                .addName(Component.text("Spawn Locations", NamedTextColor.AQUA, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current spawns: ${gamePattern.gameLocationManager.spawnLocations.size}", NamedTextColor.WHITE),
                    Component.empty(),
                    Component.text("Click to manage spawns", NamedTextColor.GREEN)
                )
                .addAmount(maxOf(1, gamePattern.gameLocationManager.spawnLocations.size))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "manage_spawns"
                }
                .build()
        )
    }

    private fun setActionButtons(inventory: Inventory) {
        inventory.setItem(
            49,
            ItemBuilder(Material.EMERALD_BLOCK)
                .addName(Component.text("✓ Save Changes", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(Component.text("Click to save and apply changes", NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "save"
                }
                .build()
        )

        inventory.setItem(
            48,
            ItemBuilder(Material.ARROW)
                .addName(Component.text("← Back", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(Component.text("Return to game list", NamedTextColor.GRAY))
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "back"
                }
                .build()
        )

        inventory.setItem(
            53,
            ItemBuilder(Material.REDSTONE_BLOCK)
                .addName(Component.text("✗ Delete Game", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(
                    Component.text("Delete this game pattern", NamedTextColor.GRAY),
                    Component.text("⚠ This cannot be undone!", NamedTextColor.DARK_RED, TextDecoration.BOLD)
                )
                .modifyMeta { meta ->
                    meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING] = "delete"
                }
                .build()
        )
    }

    /**
     * Gets the game name from an item's PDC.
     */
    fun getGameName(item: org.bukkit.inventory.ItemStack?): String? {
        val meta = item?.itemMeta ?: return null
        return meta.persistentDataContainer[KEY_GAME_NAME, PersistentDataType.STRING]
    }

    /**
     * Gets the action from an item's PDC.
     */
    fun getAction(item: org.bukkit.inventory.ItemStack?): String? {
        val meta = item?.itemMeta ?: return null
        return meta.persistentDataContainer[KEY_ACTION, PersistentDataType.STRING]
    }

    /**
     * Holder for the game list inventory.
     */
    class GameListHolder : InventoryHolder {
        private lateinit var inv: Inventory

        fun setInventory(inventory: Inventory) {
            this.inv = inventory
        }

        override fun getInventory(): Inventory = inv
    }

    /**
     * Holder for the game edit inventory.
     */
    class GameEditHolder(val gamePattern: GamePattern) : InventoryHolder {
        private lateinit var inv: Inventory

        fun setInventory(inventory: Inventory) {
            this.inv = inventory
        }

        override fun getInventory(): Inventory = inv
    }
}

