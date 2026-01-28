package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.economy.CoinManager
import com.carinaschoppe.skylife.economy.KitUnlockManager
import com.carinaschoppe.skylife.game.kit.Kit
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

/**
 * Confirmation GUI for purchasing kits.
 * Shows kit details, player balance, and confirm/cancel buttons.
 */
object KitPurchaseConfirmGui {

    private val pendingPurchases = ConcurrentHashMap<Player, Kit>()

    /**
     * Opens the purchase confirmation GUI for a kit.
     * Displays kit info, price, player balance, and confirm/cancel buttons.
     *
     * @param player The player viewing the confirmation
     * @param kit The kit being considered for purchase
     */
    fun open(player: Player, kit: Kit) {
        pendingPurchases[player] = kit

        val inventory = Bukkit.createInventory(
            null,
            27,
            Component.text("Purchase ${kit.name}?", NamedTextColor.DARK_GRAY, TextDecoration.BOLD)
        )

        val playerCoins = CoinManager.getCoins(player.uniqueId)
        val price = kit.rarity.price

        // Kit info in center
        val kitInfo = ItemStack(kit.icon.material)
        val kitMeta = kitInfo.itemMeta
        kitMeta.displayName(
            Component.text(kit.name, kit.rarity.color, TextDecoration.BOLD)
        )
        kitMeta.lore(
            listOf(
                Component.empty(),
                Component.text("Rarity: ", NamedTextColor.GRAY)
                    .append(kit.rarity.getColoredName()),
                Component.text("Price: ", NamedTextColor.GRAY)
                    .append(Component.text("$price Coins", NamedTextColor.GOLD)),
                Component.empty(),
                Component.text("Your Balance: ", NamedTextColor.GRAY)
                    .append(Component.text("$playerCoins Coins", NamedTextColor.GOLD)),
                Component.empty()
            )
        )
        kitInfo.itemMeta = kitMeta
        inventory.setItem(13, kitInfo)

        // Confirm (Green Wool)
        val confirm = ItemStack(Material.GREEN_WOOL)
        val confirmMeta = confirm.itemMeta
        confirmMeta.displayName(
            Component.text("✓ CONFIRM PURCHASE", NamedTextColor.GREEN, TextDecoration.BOLD)
        )
        if (playerCoins >= price) {
            confirmMeta.lore(
                listOf(
                    Component.text("Click to buy this kit!", NamedTextColor.GRAY),
                    Component.text("This cannot be undone!", NamedTextColor.YELLOW)
                )
            )
        } else {
            confirmMeta.lore(
                listOf(
                    Component.text("INSUFFICIENT COINS!", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text("Need ${price - playerCoins} more coins", NamedTextColor.RED)
                )
            )
        }
        confirm.itemMeta = confirmMeta
        inventory.setItem(11, confirm)

        // Cancel (Red Wool)
        val cancel = ItemStack(Material.RED_WOOL)
        val cancelMeta = cancel.itemMeta
        cancelMeta.displayName(
            Component.text("✗ CANCEL", NamedTextColor.RED, TextDecoration.BOLD)
        )
        cancelMeta.lore(
            listOf(
                Component.text("Click to go back", NamedTextColor.GRAY)
            )
        )
        cancel.itemMeta = cancelMeta
        inventory.setItem(15, cancel)

        // Fill empty slots with gray pane
        val filler = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val fillerMeta = filler.itemMeta
        fillerMeta.displayName(Component.empty())
        filler.itemMeta = fillerMeta

        for (i in 0 until 27) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler)
            }
        }

        player.openInventory(inventory)
    }

    /**
     * Handles click events in the purchase confirmation GUI.
     * Slot 11: Confirm purchase (deducts coins and unlocks kit)
     * Slot 15: Cancel purchase (returns to kit selector)
     *
     * @param player The player clicking
     * @param slot The inventory slot clicked
     * @return true if the click was handled, false if not a valid action slot
     */
    fun handleClick(player: Player, slot: Int): Boolean {
        val kit = pendingPurchases[player] ?: return false

        when (slot) {
            11 -> { // Confirm
                val result = KitUnlockManager.purchaseKit(player.uniqueId, kit)
                if (result.isSuccess) {
                    player.sendMessage(Messages.KIT_PURCHASED(kit.name))
                    player.sendMessage(
                        Messages.PREFIX.append(
                            Component.text("New balance: ", NamedTextColor.GRAY)
                                .append(Component.text("${CoinManager.getCoins(player.uniqueId)} Coins", NamedTextColor.GOLD))
                        )
                    )
                    // Reopen kit selector to show the unlocked kit
                    player.closeInventory()
                    pendingPurchases.remove(player)
                    // Schedule on next tick to avoid GUI issues
                    Bukkit.getScheduler().runTask(com.carinaschoppe.skylife.Skylife.instance, Runnable {
                        com.carinaschoppe.skylife.game.kit.KitSelectorGui.open(player)
                    })
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Purchase failed!"
                    player.sendMessage(Messages.KIT_PURCHASE_FAILED(errorMessage))
                    player.closeInventory()
                    pendingPurchases.remove(player)
                }
                return true
            }

            15 -> { // Cancel
                player.sendMessage(Messages.PREFIX.append(Component.text("Purchase cancelled", NamedTextColor.YELLOW)))
                pendingPurchases.remove(player)
                // Reopen kit selector
                player.closeInventory()
                Bukkit.getScheduler().runTask(com.carinaschoppe.skylife.Skylife.instance, Runnable {
                    com.carinaschoppe.skylife.game.kit.KitSelectorGui.open(player)
                })
                return true
            }
        }

        return false
    }

    /**
     * Checks if an inventory is a purchase confirmation GUI.
     * Used by event listeners to identify which GUI is open.
     *
     * @param inventory The inventory to check
     * @return true if this is a purchase confirmation GUI, false otherwise
     */
    fun isPurchaseGui(inventory: Inventory): Boolean {
        val title = inventory.viewers.firstOrNull()?.openInventory?.title() ?: return false
        val plainTitle = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(title)
        return plainTitle.contains("Purchase") && plainTitle.contains("?")
    }

    /**
     * Cleans up pending purchase data when player closes the GUI.
     * Should be called from inventory close event handler.
     *
     * @param player The player whose data should be cleaned up
     */
    fun cleanup(player: Player) {
        pendingPurchases.remove(player)
    }
}
