package com.carinaschoppe.skylife.skills

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
 * GUI for skill selection.
 * Players can select up to 2 skills that will be active in games.
 */
class SkillsGui(private val player: Player) : InventoryHolder {

    companion object {
        private const val INVENTORY_SIZE = 54
        private const val TITLE = "Select Your Skills"

        /**
         * Creates the skills menu item for player inventories.
         */
        fun createSkillsMenuItem(): ItemStack {
            val item = ItemStack(Material.NETHER_STAR)
            val meta = item.itemMeta

            meta.displayName(Component.text("Skills", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
            meta.lore(
                listOf(
                    Component.text("Click to select your skills", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("You can choose up to ", NamedTextColor.YELLOW)
                        .append(Component.text("2 skills", NamedTextColor.GOLD, TextDecoration.BOLD))
                )
            )

            item.itemMeta = meta
            return item
        }
    }

    private val inventory: Inventory = Bukkit.createInventory(
        this,
        INVENTORY_SIZE,
        Component.text(TITLE, NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
    )

    init {
        setupInventory()
    }

    /**
     * Sets up the inventory with skills and glass panes.
     */
    private fun setupInventory() {
        val selectedSkills = SkillsManager.getSelectedSkills(player)

        // Create glass pane filler
        val glassPane = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val glassMeta = glassPane.itemMeta
        glassMeta.displayName(Component.empty())
        glassPane.itemMeta = glassMeta

        // Fill entire inventory with glass panes first
        for (i in 0 until INVENTORY_SIZE) {
            inventory.setItem(i, glassPane)
        }

        // Place skills in specific slots
        val skillSlots = listOf(
            10, 11, 12, 13, 14, 15, 16, // Row 2
            19, 20, 21, 22, 23, 24, 25  // Row 3
        )

        val skills = Skill.values()
        skills.forEachIndexed { index, skill ->
            if (index < skillSlots.size) {
                val slot = skillSlots[index]
                val isSelected = selectedSkills.contains(skill)
                inventory.setItem(slot, skill.toItemStack(isSelected))
            }
        }

        // Add info item at bottom center
        val infoItem = createInfoItem(selectedSkills.size)
        inventory.setItem(49, infoItem)
    }

    /**
     * Creates an info item showing selected skill count.
     */
    private fun createInfoItem(selectedCount: Int): ItemStack {
        val item = ItemStack(Material.BOOK)
        val meta = item.itemMeta

        meta.displayName(Component.text("Skill Selection Info", NamedTextColor.AQUA, TextDecoration.BOLD))
        meta.lore(
            listOf(
                Component.empty(),
                Component.text("Selected: ", NamedTextColor.GRAY)
                    .append(Component.text("$selectedCount/${SkillsManager.MAX_SKILLS}", NamedTextColor.GREEN)),
                Component.empty(),
                Component.text("Click skills to select/unselect them", NamedTextColor.YELLOW)
            )
        )

        item.itemMeta = meta
        return item
    }

    /**
     * Opens the GUI for the player.
     */
    fun open() {
        player.openInventory(inventory)
    }

    /**
     * Refreshes the GUI to reflect current selections.
     */
    fun refresh() {
        setupInventory()
    }

    override fun getInventory(): Inventory {
        return inventory
    }

    /**
     * Handles click on a skill item.
     * @param skill The skill that was clicked
     */
    fun handleSkillClick(skill: Skill) {
        val result = SkillsManager.toggleSkill(player, skill)

        if (result.isSuccess) {
            val selected = result.getOrNull()!!
            if (selected) {
                player.sendMessage(
                    Messages.PREFIX.append(
                        Component.text("Skill ", Messages.MESSAGE_COLOR)
                            .append(Component.text(skill.displayName, NamedTextColor.GOLD, TextDecoration.BOLD))
                            .append(Component.text(" selected!", Messages.MESSAGE_COLOR))
                    )
                )
            } else {
                player.sendMessage(
                    Messages.PREFIX.append(
                        Component.text("Skill ", Messages.MESSAGE_COLOR)
                            .append(Component.text(skill.displayName, NamedTextColor.GOLD, TextDecoration.BOLD))
                            .append(Component.text(" unselected!", Messages.MESSAGE_COLOR))
                    )
                )
            }
            refresh()
        } else {
            player.sendMessage(
                Messages.PREFIX.append(
                    Component.text(result.exceptionOrNull()?.message ?: "Failed to select skill", Messages.ERROR_COLOR)
                )
            )
        }
    }
}
