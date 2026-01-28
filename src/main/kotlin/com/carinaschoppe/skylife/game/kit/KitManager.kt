package com.carinaschoppe.skylife.game.kit

import com.carinaschoppe.skylife.Skylife
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

/**
 * A singleton object that manages all kits and player kit selections.
 * Supports multi-kit selection with rank-based slot limits.
 */
object KitManager {
    /** A list of all available kits. */
    val kits = mutableListOf<Kit>()

    /** A map tracking which Kits each Player has selected (multi-kit support). */
    val playerKits = mutableMapOf<Player, MutableList<Kit>>()

    /**
     * Returns whether kits are enabled in the configuration.
     *
     * @return true if kits are enabled, false otherwise
     */
    fun areKitsEnabled(): Boolean {
        return Skylife.config.kitsEnabled
    }

    /**
     * Initializes all available kits using the [KitBuilder].
     * This should be called once when the plugin starts.
     * If kits are disabled in config, this will clear all kits.
     */
    fun initializeKits() {
        // Clear existing kits to prevent duplicates on reload
        kits.clear()

        // Check if kits are enabled in config
        if (!areKitsEnabled()) {
            return
        }

        val soldierKit = KitBuilder("Soldier")
            .icon(KitItem(Material.DIAMOND_SWORD, name = "<red>Soldier Kit</red>"))
            .item(KitItem(Material.DIAMOND_SWORD, enchantments = mapOf(Enchantment.SHARPNESS to 1)))
            .item(KitItem(Material.IRON_HELMET))
            .item(KitItem(Material.IRON_CHESTPLATE))
            .item(KitItem(Material.IRON_LEGGINGS))
            .item(KitItem(Material.IRON_BOOTS))
            .rarity(KitRarity.COMMON)
            .build()

        val archerKit = KitBuilder("Archer")
            .icon(KitItem(Material.BOW, name = "<green>Archer Kit</green>"))
            .rarity(KitRarity.RARE)
            .item(KitItem(Material.BOW, enchantments = mapOf(Enchantment.POWER to 1)))
            .item(KitItem(Material.ARROW, 32))
            .item(KitItem(Material.LEATHER_HELMET))
            .item(KitItem(Material.LEATHER_CHESTPLATE))
            .item(KitItem(Material.LEATHER_LEGGINGS))
            .item(KitItem(Material.LEATHER_BOOTS))
            .build()

        kits.add(soldierKit)
        kits.add(archerKit)
    }

    /**
     * Adds a kit to player's selection.
     * Checks unlock status and slot limit based on rank.
     *
     * @param player The player selecting the kit.
     * @param kit The kit being selected.
     * @return true if successful, false if kit is locked or slot limit reached
     */
    fun selectKit(player: Player, kit: Kit): Boolean {
        // Check if player has unlocked this kit
        if (!com.carinaschoppe.skylife.economy.KitUnlockManager.hasUnlocked(player.uniqueId, kit)) {
            return false
        }

        val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(player)
        val selectedKits = playerKits.getOrPut(player) { mutableListOf() }

        // Check if already selected
        if (selectedKits.contains(kit)) {
            return false
        }

        // Check slot limit
        if (selectedKits.size >= rank.maxKitSlots) {
            return false
        }

        selectedKits.add(kit)
        return true
    }

    /**
     * Removes a kit from player's selection.
     *
     * @param player The player deselecting the kit
     * @param kit The kit to deselect
     */
    fun deselectKit(player: Player, kit: Kit) {
        playerKits[player]?.remove(kit)
    }

    /**
     * Gets all selected kits for a player.
     *
     * @param player The player whose kits to retrieve
     * @return Immutable list of selected kits (empty if none selected)
     */
    fun getSelectedKits(player: Player): List<Kit> {
        return playerKits[player]?.toList() ?: emptyList()
    }

    /**
     * Retrieves the kit currently selected by a player.
     * @deprecated Use getSelectedKits() for multi-kit support
     *
     * @param player The player whose selected kit is to be retrieved.
     * @return The selected [Kit], or null if no kit is selected.
     */
    @Deprecated("Use getSelectedKits() for multi-kit support")
    fun getSelectedKit(player: Player): Kit? {
        return playerKits[player]?.firstOrNull()
    }

    /**
     * Clears the player's inventory and gives them the items from all their selected kits.
     * If no kits are selected, gives the first available kit as default.
     *
     * @param player The player to give the kit items to.
     */
    fun giveKitItems(player: Player) {
        var selectedKits = getSelectedKits(player)

        // If no kits selected, use first kit as default
        if (selectedKits.isEmpty() && kits.isNotEmpty()) {
            val defaultKit = kits.first()
            selectKit(player, defaultKit)
            selectedKits = listOf(defaultKit)
        }

        if (selectedKits.isEmpty()) return

        // Don't clear inventory here - it's already cleared in IngameState
        // Give items from all selected kits
        selectedKits.forEach { kit ->
            kit.items.forEach { kitItem ->
                val itemStack = kitItem.toItemStack()
                // Add armor to armor slots, rest to inventory
                when (itemStack.type.toString()) {
                    "LEATHER_HELMET", "CHAINMAIL_HELMET", "IRON_HELMET", "GOLDEN_HELMET", "DIAMOND_HELMET", "NETHERITE_HELMET" -> {
                        // Only set if slot is empty, priority to last kit
                        if (player.inventory.helmet == null || player.inventory.helmet?.type == Material.AIR) {
                            player.inventory.helmet = itemStack
                        }
                    }

                    "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE", "IRON_CHESTPLATE", "GOLDEN_CHESTPLATE", "DIAMOND_CHESTPLATE", "NETHERITE_CHESTPLATE" -> {
                        if (player.inventory.chestplate == null || player.inventory.chestplate?.type == Material.AIR) {
                            player.inventory.chestplate = itemStack
                        }
                    }

                    "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS", "IRON_LEGGINGS", "GOLDEN_LEGGINGS", "DIAMOND_LEGGINGS", "NETHERITE_LEGGINGS" -> {
                        if (player.inventory.leggings == null || player.inventory.leggings?.type == Material.AIR) {
                            player.inventory.leggings = itemStack
                        }
                    }

                    "LEATHER_BOOTS", "CHAINMAIL_BOOTS", "IRON_BOOTS", "GOLDEN_BOOTS", "DIAMOND_BOOTS", "NETHERITE_BOOTS" -> {
                        if (player.inventory.boots == null || player.inventory.boots?.type == Material.AIR) {
                            player.inventory.boots = itemStack
                        }
                    }

                    else -> {
                        player.inventory.addItem(itemStack)
                    }
                }
            }
        }
    }

    /**
     * Removes a player's kit selection, typically when they disconnect or leave a game.
     *
     * @param player The player to remove.
     */
    fun removePlayer(player: Player) {
        playerKits.remove(player)
    }
}
