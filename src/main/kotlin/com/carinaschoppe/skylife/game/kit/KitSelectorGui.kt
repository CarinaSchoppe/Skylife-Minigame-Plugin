package com.carinaschoppe.skylife.game.kit

import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Manages the creation and display of the kit selector graphical user interface (GUI).
 */
object KitSelectorGui {

    /** The title displayed at the top of the kit selector GUI. */
    const val GUI_TITLE = "§8Select your Kit"

    private val PLACEHOLDER_ITEM: ItemStack = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
        val meta = itemMeta
        meta.displayName(Component.text(" "))
        itemMeta = meta
    }

    /**
     * Creates and opens the kit selector GUI for a specific player.
     *
     * @param player The player to whom the GUI will be shown.
     */
    fun open(player: Player) {
        val inventorySize = 27 // 3 rows, can be adjusted
        val inventory = Bukkit.createInventory(null, inventorySize, Messages.legacy(GUI_TITLE))

        addKitIcons(inventory)
        fillPlaceholder(inventory)

        player.openInventory(inventory)
    }

    /**
     * Populates the inventory with icons representing each available kit.
     *
     * @param inventory The inventory to populate.
     */
    private fun addKitIcons(inventory: Inventory) {
        KitManager.kits.forEachIndexed { index, kit ->
            if (index < inventory.size) {
                inventory.setItem(index, createKitIcon(kit))
            }
        }
    }

    /**
     * Creates the ItemStack used as an icon for a specific kit.
     * The icon's lore dynamically lists the items contained within the kit.
     *
     * @param kit The kit for which to create an icon.
     * @return The fully assembled ItemStack icon.
     */
    private fun createKitIcon(kit: Kit): ItemStack {
        val iconStack = kit.icon.toItemStack()
        val meta = iconStack.itemMeta

        // Dynamically generate the lore based on kit contents
        val lore = mutableListOf<Component>()
        lore.add(Component.text(" "))
        lore.add(Component.text("Items:", NamedTextColor.GRAY))
        kit.items.forEach { kitItem ->
            val itemName = kitItem.name ?: kitItem.material.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
            lore.add(Component.text(" - ", NamedTextColor.DARK_GRAY).append(Component.text("${kitItem.amount}x $itemName", NamedTextColor.AQUA)))
        }
        lore.add(Component.text(" "))
        lore.add(Component.text("Click to select!", NamedTextColor.YELLOW, TextDecoration.BOLD))

        //TODO: deprecated
        meta.loreComponents(lore)
        iconStack.itemMeta = meta
        return iconStack
    }

    /**
     * Fills all empty slots in the inventory with a placeholder item.
     *
     * @param inventory The inventory to fill.
     */
    private fun fillPlaceholder(inventory: Inventory) {
        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, PLACEHOLDER_ITEM)
            }
        }
    }
}