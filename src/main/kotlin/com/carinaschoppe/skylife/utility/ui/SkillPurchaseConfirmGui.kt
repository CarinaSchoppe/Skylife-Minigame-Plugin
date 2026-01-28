package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.economy.CoinManager
import com.carinaschoppe.skylife.skills.Skill
import com.carinaschoppe.skylife.skills.SkillUnlockManager
import com.carinaschoppe.skylife.utility.messages.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * Confirmation GUI for purchasing skills.
 * Shows the skill details and confirm/cancel buttons.
 */
class SkillPurchaseConfirmGui(private val player: Player, val skill: Skill) : InventoryHolder {

    companion object {
        private const val INVENTORY_SIZE = 27
    }

    private val inventory: Inventory = Bukkit.createInventory(
        this,
        INVENTORY_SIZE,
        Component.text("Purchase ${skill.displayName}?", NamedTextColor.GOLD, TextDecoration.BOLD)
    )

    init {
        setupInventory()
    }

    /**
     * Sets up the confirmation GUI layout.
     */
    private fun setupInventory() {
        // Create glass pane filler
        val glassPane = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val glassMeta = glassPane.itemMeta
        glassMeta.displayName(Component.empty())
        glassPane.itemMeta = glassMeta

        // Fill entire inventory with glass panes
        for (i in 0 until INVENTORY_SIZE) {
            inventory.setItem(i, glassPane)
        }

        // Skill item in the center top
        inventory.setItem(13, skill.toItemStack(selected = false, unlocked = false))

        // Confirm button (green glass)
        val confirmItem = createConfirmItem()
        inventory.setItem(11, confirmItem)

        // Cancel button (red glass)
        val cancelItem = createCancelItem()
        inventory.setItem(15, cancelItem)

        // Info item
        val infoItem = createInfoItem()
        inventory.setItem(22, infoItem)
    }

    /**
     * Creates the confirm purchase button.
     */
    private fun createConfirmItem(): ItemStack {
        val item = ItemStack(Material.LIME_STAINED_GLASS_PANE)
        val meta = item.itemMeta

        meta.displayName(Component.text("✔ CONFIRM PURCHASE", NamedTextColor.GREEN, TextDecoration.BOLD))

        val currentCoins = CoinManager.getCoins(player.uniqueId)
        val canAfford = currentCoins >= skill.rarity.price

        val lore = mutableListOf<Component>()
        lore.add(Component.empty())
        lore.add(
            Component.text("Price: ", NamedTextColor.GRAY)
                .append(Component.text("${skill.rarity.price} coins", NamedTextColor.YELLOW))
        )
        lore.add(
            Component.text("Your coins: ", NamedTextColor.GRAY)
                .append(Component.text("$currentCoins", if (canAfford) NamedTextColor.GREEN else NamedTextColor.RED))
        )
        lore.add(Component.empty())

        if (canAfford) {
            lore.add(Component.text("Click to purchase!", NamedTextColor.GREEN))
        } else {
            lore.add(Component.text("You cannot afford this!", NamedTextColor.RED, TextDecoration.BOLD))
        }

        meta.lore(lore)
        item.itemMeta = meta

        return item
    }

    /**
     * Creates the cancel button.
     */
    private fun createCancelItem(): ItemStack {
        val item = ItemStack(Material.RED_STAINED_GLASS_PANE)
        val meta = item.itemMeta

        meta.displayName(Component.text("✖ CANCEL", NamedTextColor.RED, TextDecoration.BOLD))
        meta.lore(
            listOf(
                Component.empty(),
                Component.text("Go back to skill selection", NamedTextColor.GRAY)
            )
        )

        item.itemMeta = meta
        return item
    }

    /**
     * Creates the info item showing rarity details.
     */
    private fun createInfoItem(): ItemStack {
        val item = ItemStack(Material.BOOK)
        val meta = item.itemMeta

        meta.displayName(Component.text("Purchase Information", NamedTextColor.AQUA, TextDecoration.BOLD))
        meta.lore(
            listOf(
                Component.empty(),
                Component.text("Rarity: ", NamedTextColor.GRAY)
                    .append(skill.rarity.getColoredName()),
                Component.text("Price: ", NamedTextColor.GRAY)
                    .append(Component.text("${skill.rarity.price} coins", NamedTextColor.YELLOW)),
                Component.empty(),
                Component.text("Unlock this skill permanently!", NamedTextColor.GOLD)
            )
        )

        item.itemMeta = meta
        return item
    }

    /**
     * Opens the confirmation GUI for the player.
     */
    fun open() {
        player.openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        return inventory
    }

    /**
     * Handles the confirm button click.
     * Attempts to purchase the skill and opens the skills GUI again.
     */
    fun handleConfirm() {
        val result = SkillUnlockManager.purchaseSkill(player.uniqueId, skill)

        if (result.isSuccess) {
            player.sendMessage(
                Messages.PREFIX.append(
                    Component.text("Successfully purchased ", Messages.MESSAGE_COLOR)
                        .append(Component.text(skill.displayName, skill.rarity.color, TextDecoration.BOLD))
                        .append(Component.text("!", Messages.MESSAGE_COLOR))
                )
            )
            player.playSound(player.location, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        } else {
            player.sendMessage(
                Messages.PREFIX.append(
                    Component.text(result.exceptionOrNull()?.message ?: "Failed to purchase skill", Messages.ERROR_COLOR)
                )
            )
            player.playSound(player.location, org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
        }

        // Return to skills GUI
        SkillsGui(player).open()
    }

    /**
     * Handles the cancel button click.
     * Returns to the skills GUI.
     */
    fun handleCancel() {
        SkillsGui(player).open()
    }
}
