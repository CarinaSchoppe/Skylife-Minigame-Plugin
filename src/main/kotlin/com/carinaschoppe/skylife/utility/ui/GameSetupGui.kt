package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.game.GamePattern
import com.carinaschoppe.skylife.utility.ui.inventoryholders.GameSetupHolderFactory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * GUI for setting up a new game pattern.
 */
class GameSetupGui(override val player: Player, override val gamePattern: GamePattern) : GameSetupHolderFactory(player, gamePattern) {

    companion object {
        // Slot positions
        const val MIN_PLAYERS_DISPLAY = 10
        const val MIN_PLAYERS_DECREASE = 9
        const val MIN_PLAYERS_INCREASE = 11

        const val MAX_PLAYERS_DISPLAY = 19
        const val MAX_PLAYERS_DECREASE = 18
        const val MAX_PLAYERS_INCREASE = 20

        const val LOBBY_LOCATION = 28
        const val SPECTATOR_LOCATION = 29
        const val MAIN_LOCATION = 30
        const val SPAWN_LOCATIONS = 31

        const val FINISH_SLOT = 49
        const val CANCEL_SLOT = 48
    }

    override fun initInventory(): GameSetupHolderFactory {
        super.initInventory()
        updateInventory()
        return this
    }

    /**
     * Updates all items in the inventory based on current pattern state.
     */
    fun updateInventory() {
        val builder = GUIBuilder(this)

        // Min Players controls
        builder.setItem(
            MIN_PLAYERS_DECREASE,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Min Players", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text("Click to decrease", NamedTextColor.GRAY))
                .build()
        )

        builder.setItem(
            MIN_PLAYERS_DISPLAY,
            ItemBuilder(Material.PLAYER_HEAD)
                .addName(Component.text("Min Players", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current: ${gamePattern.minPlayers}", NamedTextColor.WHITE),
                    Component.text("(Minimum: 1)", NamedTextColor.GRAY)
                )
                .addAmount(maxOf(1, gamePattern.minPlayers))
                .build()
        )

        builder.setItem(
            MIN_PLAYERS_INCREASE,
            ItemBuilder(Material.LIME_WOOL)
                .addName(Component.text("Increase Min Players", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(Component.text("Click to increase", NamedTextColor.GRAY))
                .build()
        )

        // Max Players controls
        builder.setItem(
            MAX_PLAYERS_DECREASE,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Max Players", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text("Click to decrease", NamedTextColor.GRAY))
                .build()
        )

        builder.setItem(
            MAX_PLAYERS_DISPLAY,
            ItemBuilder(Material.PLAYER_HEAD)
                .addName(Component.text("Max Players", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current: ${gamePattern.maxPlayers}", NamedTextColor.WHITE),
                    Component.text("(Must be ≥ Min Players)", NamedTextColor.GRAY)
                )
                .addAmount(maxOf(1, gamePattern.maxPlayers))
                .build()
        )

        builder.setItem(
            MAX_PLAYERS_INCREASE,
            ItemBuilder(Material.LIME_WOOL)
                .addName(Component.text("Increase Max Players", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(Component.text("Click to increase", NamedTextColor.GRAY))
                .build()
        )

        // Location setters
        val locationManager = gamePattern.gameLocationManager
        val lobbySet = locationManager.isLocationInitialized("lobby")
        builder.setItem(
            LOBBY_LOCATION,
            ItemBuilder(if (lobbySet) Material.LIME_WOOL else Material.WHITE_WOOL)
                .addName(Component.text("Lobby Location", if (lobbySet) NamedTextColor.GREEN else NamedTextColor.WHITE, TextDecoration.BOLD))
                .addLore(
                    if (lobbySet)
                        Component.text("✓ Location set!", NamedTextColor.GREEN)
                    else
                        Component.text("Click to set your current location", NamedTextColor.GRAY)
                )
                .build()
        )

        val spectatorSet = locationManager.isLocationInitialized("spectator")
        builder.setItem(
            SPECTATOR_LOCATION,
            ItemBuilder(if (spectatorSet) Material.LIME_WOOL else Material.WHITE_WOOL)
                .addName(Component.text("Spectator Location", if (spectatorSet) NamedTextColor.GREEN else NamedTextColor.WHITE, TextDecoration.BOLD))
                .addLore(
                    if (spectatorSet)
                        Component.text("✓ Location set!", NamedTextColor.GREEN)
                    else
                        Component.text("Click to set your current location", NamedTextColor.GRAY)
                )
                .build()
        )

        val mainSet = locationManager.isLocationInitialized("main")
        builder.setItem(
            MAIN_LOCATION,
            ItemBuilder(if (mainSet) Material.LIME_WOOL else Material.WHITE_WOOL)
                .addName(Component.text("Main Location", if (mainSet) NamedTextColor.GREEN else NamedTextColor.WHITE, TextDecoration.BOLD))
                .addLore(
                    if (mainSet)
                        Component.text("✓ Location set!", NamedTextColor.GREEN)
                    else
                        Component.text("Click to set your current location", NamedTextColor.GRAY)
                )
                .build()
        )

        // Spawn locations with counter
        val spawnCount = gamePattern.gameLocationManager.spawnLocations.size
        builder.setItem(
            SPAWN_LOCATIONS,
            ItemBuilder(Material.PLAYER_HEAD)
                .addName(Component.text("Spawn Locations", NamedTextColor.AQUA, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current spawns: $spawnCount", NamedTextColor.WHITE),
                    Component.text("Click to add your current location", NamedTextColor.GRAY),
                    Component.text("Use /removespawn <number> to remove", NamedTextColor.YELLOW)
                )
                .addAmount(maxOf(1, spawnCount))
                .build()
        )

        // Cancel button
        builder.setItem(
            CANCEL_SLOT,
            ItemBuilder(Material.RED_CONCRETE)
                .addName(Component.text("✗ Cancel Setup", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(
                    Component.text("Discard this setup without saving", NamedTextColor.GRAY),
                    Component.text("This cannot be undone!", NamedTextColor.DARK_RED)
                )
                .build()
        )

        // Finish button
        val isComplete = gamePattern.isComplete()
        builder.setItem(
            FINISH_SLOT,
            ItemBuilder(if (isComplete) Material.LIME_CONCRETE else Material.BARRIER)
                .addName(
                    Component.text(
                        if (isComplete) "✓ Finish & Save" else "✗ Incomplete Setup",
                        if (isComplete) NamedTextColor.GREEN else NamedTextColor.RED,
                        TextDecoration.BOLD
                    )
                )
                .apply {
                    if (isComplete) {
                        addLore(Component.text("Click to save the game pattern!", NamedTextColor.GREEN))
                    } else {
                        val missing = mutableListOf<String>()
                        if (gamePattern.minPlayers < 1) missing.add("Min Players must be ≥ 1")
                        if (gamePattern.maxPlayers < gamePattern.minPlayers) missing.add("Max Players must be ≥ Min Players")
                        if (!lobbySet) missing.add("Lobby Location")
                        if (!spectatorSet) missing.add("Spectator Location")
                        if (!mainSet) missing.add("Main Location")
                        if (spawnCount == 0) missing.add("At least 1 Spawn Location")

                        val lore = mutableListOf(Component.text("Missing requirements:", NamedTextColor.RED))
                        missing.forEach { lore.add(Component.text("• $it", NamedTextColor.GRAY)) }
                        addLore(*lore.toTypedArray())
                    }
                }
                .build()
        )

        // Fill empty slots with filler
        builder.fillerPanel()
    }
}
