package com.carinaschoppe.skylife.game.kit

import com.carinaschoppe.skylife.Skylife
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

/**
 * A singleton object that manages all kits and player kit selections.
 */
object KitManager {
    /** A list of all available kits. */
    val kits = mutableListOf<Kit>()

    /** A map tracking which [Kit] each [Player] has selected. */
    val playerKits = mutableMapOf<Player, Kit>()

    /**
     * Returns whether kits are enabled in the configuration.
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
            .build()

        val archerKit = KitBuilder("Archer")
            .icon(KitItem(Material.BOW, name = "<green>Archer Kit</green>"))
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
     * Assigns a selected kit to a player.
     *
     * @param player The player selecting the kit.
     * @param kit The kit being selected.
     */
    fun selectKit(player: Player, kit: Kit) {
        playerKits[player] = kit
        // Nachricht an Spieler & Scoreboard Update kommt spÃ¤ter
    }

    /**
     * Retrieves the kit currently selected by a player.
     *
     * @param player The player whose selected kit is to be retrieved.
     * @return The selected [Kit], or null if no kit is selected.
     */
    fun getSelectedKit(player: Player): Kit? {
        return playerKits[player]
    }

    /**
     * Clears the player's inventory and gives them the items from their selected kit.
     *
     * @param player The player to give the kit items to.
     */
    fun giveKitItems(player: Player) {
        val kit = getSelectedKit(player) ?: return
        player.inventory.clear()
        kit.items.forEach { kitItem ->
            player.inventory.addItem(kitItem.toItemStack())
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
