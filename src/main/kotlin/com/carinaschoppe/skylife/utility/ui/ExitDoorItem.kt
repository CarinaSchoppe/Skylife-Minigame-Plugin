package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.Skylife
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Utility for creating and identifying the exit door item.
 * When clicked, this item teleports the player to hub.
 */
object ExitDoorItem {

    private val exitDoorKey: NamespacedKey by lazy { NamespacedKey(Skylife.instance, "exit_door") }

    /**
     * Creates the exit door item for player inventories.
     */
    fun create(): ItemStack {
        val item = ItemStack(Material.OAK_DOOR)
        val meta = item.itemMeta

        meta.displayName(Component.text("Exit to Hub", NamedTextColor.RED, TextDecoration.BOLD))
        meta.lore(
            listOf(
                Component.text("Click to leave the game", NamedTextColor.GRAY),
                Component.text("and return to the hub", NamedTextColor.GRAY)
            )
        )

        meta.persistentDataContainer[exitDoorKey, PersistentDataType.BYTE] = 1.toByte()
        item.itemMeta = meta
        return item
    }

    /**
     * Checks if an item is the exit door item.
     */
    fun isExitDoor(item: ItemStack?): Boolean {
        val meta = item?.itemMeta ?: return false
        return meta.persistentDataContainer.has(exitDoorKey, PersistentDataType.BYTE)
    }
}
