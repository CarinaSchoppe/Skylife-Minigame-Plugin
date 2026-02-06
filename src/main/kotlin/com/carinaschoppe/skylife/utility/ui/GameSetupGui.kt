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
        private const val LORE_CLICK_TO_DECREASE = "Click to decrease"
        private const val LORE_CLICK_TO_INCREASE = "Click to increase"
        private const val LORE_CLICK_TO_SET_LOCATION = "Click to set your current location"
        private const val LOCATION_SET_TEXT = "✓ Location set!"
        private const val LOBBY_LOCATION_LABEL = "Lobby Location"
        private const val SPECTATOR_LOCATION_LABEL = "Spectator Location"
        private const val MAIN_LOCATION_LABEL = "Main Location"
        private const val FINISH_TITLE = "✓ Finish & Save"
        private const val INCOMPLETE_TITLE = "✗ Incomplete Setup"
        private const val FINISH_LORE = "Click to save the game pattern!"
        private const val MISSING_REQUIREMENTS_TITLE = "Missing requirements:"
        private const val MISSING_MIN_PLAYERS = "Min Players must be ≥ 1"
        private const val MISSING_MAX_PLAYERS = "Max Players must be ≥ Min Players"
        private const val MISSING_SPAWN_LOCATION = "At least 1 Spawn Location"
        private const val MISSING_BULLET_PREFIX = "• "

        // Slot positions
        const val MIN_PLAYERS_DISPLAY = 10
        const val MIN_PLAYERS_DECREASE = 9
        const val MIN_PLAYERS_INCREASE = 11

        const val MAX_PLAYERS_DISPLAY = 19
        const val MAX_PLAYERS_DECREASE = 18
        const val MAX_PLAYERS_INCREASE = 20

        const val MIN_TO_START_DISPLAY = 13
        const val MIN_TO_START_DECREASE = 12
        const val MIN_TO_START_INCREASE = 14

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

        setMinPlayersItems(builder)
        setMaxPlayersItems(builder)
        setMinPlayersToStartItems(builder)

        val locationState = setLocationItems(builder)
        val spawnCount = setSpawnLocations(builder)
        setCancelButton(builder)
        setFinishButton(builder, locationState, spawnCount)

        // Fill empty slots with filler
        builder.fillerPanel()
    }

    private data class LocationState(
        val lobbySet: Boolean,
        val spectatorSet: Boolean,
        val mainSet: Boolean
    )

    private fun setMinPlayersItems(builder: GUIBuilder) {
        builder.setItem(
            MIN_PLAYERS_DECREASE,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Min Players", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_DECREASE, NamedTextColor.GRAY))
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
                .addLore(Component.text(LORE_CLICK_TO_INCREASE, NamedTextColor.GRAY))
                .build()
        )
    }

    private fun setMaxPlayersItems(builder: GUIBuilder) {
        builder.setItem(
            MAX_PLAYERS_DECREASE,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Max Players", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_DECREASE, NamedTextColor.GRAY))
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
                .addLore(Component.text(LORE_CLICK_TO_INCREASE, NamedTextColor.GRAY))
                .build()
        )
    }

    private fun setMinPlayersToStartItems(builder: GUIBuilder) {
        builder.setItem(
            MIN_TO_START_DECREASE,
            ItemBuilder(Material.RED_WOOL)
                .addName(Component.text("Decrease Min to Start", NamedTextColor.RED, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_DECREASE, NamedTextColor.GRAY))
                .build()
        )

        builder.setItem(
            MIN_TO_START_DISPLAY,
            ItemBuilder(Material.CLOCK)
                .addName(Component.text("Min Players to Start", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .addLore(
                    Component.text("Current: ${gamePattern.minPlayersToStart}", NamedTextColor.WHITE),
                    Component.text("Players needed to start countdown", NamedTextColor.GRAY),
                    Component.text("(Min: 1, Max: ${gamePattern.maxPlayers})", NamedTextColor.GRAY)
                )
                .addAmount(maxOf(1, gamePattern.minPlayersToStart))
                .build()
        )

        builder.setItem(
            MIN_TO_START_INCREASE,
            ItemBuilder(Material.LIME_WOOL)
                .addName(Component.text("Increase Min to Start", NamedTextColor.GREEN, TextDecoration.BOLD))
                .addLore(Component.text(LORE_CLICK_TO_INCREASE, NamedTextColor.GRAY))
                .build()
        )
    }

    private fun setLocationItems(builder: GUIBuilder): LocationState {
        val locationManager = gamePattern.gameLocationManager
        val lobbySet = locationManager.isLocationInitialized("lobby")
        builder.setItem(
            LOBBY_LOCATION,
            ItemBuilder(if (lobbySet) Material.LIME_WOOL else Material.WHITE_WOOL)
                .addName(Component.text(LOBBY_LOCATION_LABEL, if (lobbySet) NamedTextColor.GREEN else NamedTextColor.WHITE, TextDecoration.BOLD))
                .addLore(
                    if (lobbySet)
                        Component.text(LOCATION_SET_TEXT, NamedTextColor.GREEN)
                    else
                        Component.text(LORE_CLICK_TO_SET_LOCATION, NamedTextColor.GRAY)
                )
                .build()
        )

        val spectatorSet = locationManager.isLocationInitialized("spectator")
        builder.setItem(
            SPECTATOR_LOCATION,
            ItemBuilder(if (spectatorSet) Material.LIME_WOOL else Material.WHITE_WOOL)
                .addName(Component.text(SPECTATOR_LOCATION_LABEL, if (spectatorSet) NamedTextColor.GREEN else NamedTextColor.WHITE, TextDecoration.BOLD))
                .addLore(
                    if (spectatorSet)
                        Component.text(LOCATION_SET_TEXT, NamedTextColor.GREEN)
                    else
                        Component.text(LORE_CLICK_TO_SET_LOCATION, NamedTextColor.GRAY)
                )
                .build()
        )

        val mainSet = locationManager.isLocationInitialized("main")
        builder.setItem(
            MAIN_LOCATION,
            ItemBuilder(if (mainSet) Material.LIME_WOOL else Material.WHITE_WOOL)
                .addName(Component.text(MAIN_LOCATION_LABEL, if (mainSet) NamedTextColor.GREEN else NamedTextColor.WHITE, TextDecoration.BOLD))
                .addLore(
                    if (mainSet)
                        Component.text(LOCATION_SET_TEXT, NamedTextColor.GREEN)
                    else
                        Component.text(LORE_CLICK_TO_SET_LOCATION, NamedTextColor.GRAY)
                )
                .build()
        )

        return LocationState(lobbySet, spectatorSet, mainSet)
    }

    private fun setSpawnLocations(builder: GUIBuilder): Int {
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
        return spawnCount
    }

    private fun setCancelButton(builder: GUIBuilder) {
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
    }

    private fun setFinishButton(builder: GUIBuilder, locationState: LocationState, spawnCount: Int) {
        val isComplete = gamePattern.isComplete()
        val item = ItemBuilder(if (isComplete) Material.LIME_CONCRETE else Material.BARRIER)
            .addName(
                Component.text(
                    if (isComplete) FINISH_TITLE else INCOMPLETE_TITLE,
                    if (isComplete) NamedTextColor.GREEN else NamedTextColor.RED,
                    TextDecoration.BOLD
                )
            )
            .apply { addFinishLore(isComplete, locationState, spawnCount) }
            .build()

        builder.setItem(FINISH_SLOT, item)
    }

    private fun ItemBuilder.addFinishLore(isComplete: Boolean, locationState: LocationState, spawnCount: Int) {
        if (isComplete) {
            addLore(Component.text(FINISH_LORE, NamedTextColor.GREEN))
            return
        }

        val missing = collectMissingRequirements(locationState, spawnCount)
        val lore = mutableListOf(Component.text(MISSING_REQUIREMENTS_TITLE, NamedTextColor.RED))
        missing.forEach { requirement ->
            lore.add(Component.text("$MISSING_BULLET_PREFIX$requirement", NamedTextColor.GRAY))
        }
        addLore(*lore.toTypedArray())
    }

    private fun collectMissingRequirements(locationState: LocationState, spawnCount: Int): List<String> {
        val missing = mutableListOf<String>()
        if (gamePattern.minPlayers < 1) missing.add(MISSING_MIN_PLAYERS)
        if (gamePattern.maxPlayers < gamePattern.minPlayers) missing.add(MISSING_MAX_PLAYERS)
        if (!locationState.lobbySet) missing.add(LOBBY_LOCATION_LABEL)
        if (!locationState.spectatorSet) missing.add(SPECTATOR_LOCATION_LABEL)
        if (!locationState.mainSet) missing.add(MAIN_LOCATION_LABEL)
        if (spawnCount == 0) missing.add(MISSING_SPAWN_LOCATION)
        return missing
    }
}
