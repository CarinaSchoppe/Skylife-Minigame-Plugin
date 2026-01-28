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
    const val GUI_TITLE = "<dark_gray>Select your Kit</dark_gray>"

    private val PLACEHOLDER_ITEM: ItemStack = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
        val meta = itemMeta
        meta.displayName(Component.text(" "))
        itemMeta = meta
    }

    /**
     * Creates the kit selector item that players can click to open the kit GUI.
     *
     * @return The kit selector ItemStack.
     */
    fun createKitSelectorItem(): ItemStack {
        return ItemStack(Material.CHEST).apply {
            val meta = itemMeta
            meta.displayName(Messages.parse("<green><bold>Kit Selector</bold></green>"))
            itemMeta = meta
        }
    }

    /**
     * Creates and opens the kit selector GUI for a specific player.
     *
     * @param player The player to whom the GUI will be shown.
     */
    fun open(player: Player) {
        val inventorySize = 27 // 3 rows, can be adjusted
        val inventory = Bukkit.createInventory(null, inventorySize, Messages.parse(GUI_TITLE))

        addKitIcons(inventory, player)
        addKitSlotInfo(inventory, player)
        fillPlaceholder(inventory)

        player.openInventory(inventory)
    }

    /**
     * Populates the inventory with icons representing each available kit.
     *
     * @param inventory The inventory to populate.
     * @param player The player viewing the GUI.
     */
    private fun addKitIcons(inventory: Inventory, player: Player) {
        KitManager.kits.forEachIndexed { index, kit ->
            if (index < inventory.size) {
                inventory.setItem(index, createKitIcon(kit, player))
            }
        }
    }

    /**
     * Adds info about kit slot usage at the bottom of the GUI.
     *
     * @param inventory The inventory to populate.
     * @param player The player viewing the GUI.
     */
    private fun addKitSlotInfo(inventory: Inventory, player: Player) {
        val rank = com.carinaschoppe.skylife.economy.PlayerRank.getRank(player)
        val selectedKits = KitManager.getSelectedKits(player)

        val infoItem = ItemStack(Material.PAPER).apply {
            val meta = itemMeta
            meta.displayName(Component.text("Kit Slots", NamedTextColor.GOLD, TextDecoration.BOLD))
            val lore = mutableListOf<Component>()
            lore.add(Component.text(" "))
            lore.add(Component.text("Selected: ${selectedKits.size}/${rank.maxKitSlots}", NamedTextColor.YELLOW))
            lore.add(Component.text(" "))
            lore.add(Component.text("Your Rank: ${rank.displayName}", rank.getColor()))
            lore.add(Component.text(" "))
            lore.add(Component.text("Coins: ${com.carinaschoppe.skylife.economy.CoinManager.getCoins(player.uniqueId)}", NamedTextColor.GOLD))
            meta.lore(lore)
            itemMeta = meta
        }

        inventory.setItem(inventory.size - 5, infoItem)
    }

    /**
     * Creates the ItemStack used as an icon for a specific kit.
     * The icon's lore dynamically lists the items contained within the kit.
     * Shows lock status, prices, and selection state.
     *
     * @param kit The kit for which to create an icon.
     * @param player The player viewing the GUI.
     * @return The fully assembled ItemStack icon.
     */
    private fun createKitIcon(kit: Kit, player: Player): ItemStack {
        val isUnlocked = com.carinaschoppe.skylife.economy.KitUnlockManager.hasUnlocked(player.uniqueId, kit)
        val isSelected = KitManager.getSelectedKits(player).contains(kit)

        // Use barrier for locked kits
        val iconStack = if (!isUnlocked && kit.rarity.price > 0) {
            ItemStack(Material.BARRIER)
        } else {
            kit.icon.toItemStack()
        }

        val meta = iconStack.itemMeta

        // Update display name with selection indicator
        val baseName = kit.icon.toItemStack().itemMeta?.displayName() ?: Component.text(kit.name)
        val displayName = if (isSelected) {
            Component.text("✓ ", NamedTextColor.GREEN, TextDecoration.BOLD).append(baseName)
        } else {
            baseName
        }
        meta.displayName(displayName)

        // Dynamically generate the lore based on kit contents
        val lore = mutableListOf<Component>()
        lore.add(Component.text(" "))
        lore.add(
            Component.text("Rarity: ", NamedTextColor.GRAY)
                .append(Component.text(kit.rarity.displayName, kit.rarity.color, TextDecoration.BOLD))
        )
        lore.add(Component.text(" "))
        lore.add(Component.text("Items:", NamedTextColor.GRAY))
        kit.items.forEach { kitItem ->
            val itemName = kitItem.name ?: kitItem.material.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
            lore.add(Component.text(" - ", NamedTextColor.DARK_GRAY).append(Component.text("${kitItem.amount}x $itemName", NamedTextColor.AQUA)))
        }
        lore.add(Component.text(" "))

        // Add status and action based on unlock state
        if (!isUnlocked && kit.rarity.price > 0) {
            lore.add(Component.text("🔒 LOCKED", NamedTextColor.RED, TextDecoration.BOLD))
            lore.add(Component.text(" "))
            lore.add(Component.text("Price: ${kit.rarity.price} Coins", NamedTextColor.GOLD))
            lore.add(Component.text(" "))
            lore.add(Component.text("Click to purchase!", NamedTextColor.YELLOW, TextDecoration.BOLD))
        } else if (isSelected) {
            lore.add(Component.text("✓ SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD))
            lore.add(Component.text(" "))
            lore.add(Component.text("Click to deselect!", NamedTextColor.GRAY))
        } else {
            lore.add(Component.text("Click to select!", NamedTextColor.YELLOW, TextDecoration.BOLD))
        }

        meta.lore(lore)
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

