package com.carinaschoppe.skylife.skills

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Represents a skill/perk that players can select and use in games.
 */
enum class Skill(
    val displayName: String,
    val description: List<String>,
    val material: Material,
    val type: SkillType
) {
    JUMBO(
        "Jumbo",
        listOf(
            "You never feel hunger.",
            "Your food bar stays full at all times.",
            "",
            "<gray>Type: <yellow>Resource / Sustain</yellow></gray>"
        ),
        Material.BREAD,
        SkillType.SUSTAIN
    ),

    REGENERATOR(
        "Regenerator",
        listOf(
            "Constant regeneration keeps you",
            "alive during combat.",
            "",
            "<gray>Type: <yellow>Sustain</yellow></gray>"
        ),
        Material.HEART_OF_THE_SEA,
        SkillType.SUSTAIN
    ),

    ABSORBER(
        "Absorber",
        listOf(
            "Gain extra absorption hearts",
            "for increased durability.",
            "",
            "<gray>Type: <yellow>Defense</yellow></gray>"
        ),
        Material.GOLDEN_APPLE,
        SkillType.DEFENSE
    ),

    FEATHERFALL(
        "Featherfall",
        listOf(
            "Fall damage is significantly reduced.",
            "Safer bridging and knockback resistance.",
            "",
            "<gray>Type: <yellow>Mobility / Defense</yellow></gray>"
        ),
        Material.FEATHER,
        SkillType.MOBILITY
    ),

    SNOW_SPAMMER(
        "Snow Spammer",
        listOf(
            "You receive snowballs regularly",
            "to disrupt enemies.",
            "",
            "<gray>Type: <yellow>Utility / Control</yellow></gray>"
        ),
        Material.SNOWBALL,
        SkillType.UTILITY
    ),

    LUCKY_BIRD(
        "Lucky Bird",
        listOf(
            "A lucky bird grants unpredictable",
            "effects when used.",
            "",
            "<gray>Type: <yellow>Utility / Fun</yellow></gray>"
        ),
        Material.EGG,
        SkillType.UTILITY
    ),

    WOLFLORD(
        "Wolflord",
        listOf(
            "Summon a loyal wolf pack",
            "to fight for you.",
            "",
            "<gray>Type: <yellow>Summoner</yellow></gray>"
        ),
        Material.BONE,
        SkillType.SUMMONER
    ),

    ENDERMASTER(
        "Endermaster",
        listOf(
            "Ender Pearls regenerate over time,",
            "allowing rapid movement.",
            "",
            "<gray>Type: <yellow>Mobility</yellow></gray>"
        ),
        Material.ENDER_PEARL,
        SkillType.MOBILITY
    ),

    WITCH(
        "Witch",
        listOf(
            "Random potions appear regularly.",
            "Power comes with unpredictability.",
            "",
            "<gray>Type: <yellow>Random Utility</yellow></gray>"
        ),
        Material.BREWING_STAND,
        SkillType.UTILITY
    ),

    BUILDER(
        "Builder",
        listOf(
            "Start with a massive supply",
            "of building blocks.",
            "",
            "<gray>Type: <yellow>Economy / Control</yellow></gray>"
        ),
        Material.STONE_BRICKS,
        SkillType.UTILITY
    ),

    SWORDMASTER(
        "Swordmaster",
        listOf(
            "Begin the match armed for close",
            "combat with sword and shield.",
            "",
            "<gray>Type: <yellow>Combat</yellow></gray>"
        ),
        Material.IRON_SWORD,
        SkillType.COMBAT
    ),

    BOWMASTER(
        "Bowmaster",
        listOf(
            "Master the battlefield from range",
            "with an enchanted bow.",
            "",
            "<gray>Type: <yellow>Ranged Combat</yellow></gray>"
        ),
        Material.BOW,
        SkillType.COMBAT
    ),

    INVISIBLE_STALKER(
        "Invisible Stalker",
        listOf(
            "Become invisible under the right",
            "conditions and strike unseen.",
            "",
            "<gray>Type: <yellow>Stealth</yellow></gray>"
        ),
        Material.FERMENTED_SPIDER_EYE,
        SkillType.UTILITY
    ),

    STRENGTH_CORE(
        "Strength Core",
        listOf(
            "Increased attack damage turns",
            "you into a killing machine.",
            "",
            "<gray>Type: <yellow>Burst Combat</yellow></gray>"
        ),
        Material.BLAZE_POWDER,
        SkillType.COMBAT
    );

    /**
     * Creates an ItemStack representing this skill.
     * @param selected Whether this skill is currently selected
     */
    fun toItemStack(selected: Boolean): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta

        meta.displayName(Component.text(displayName, NamedTextColor.GOLD, TextDecoration.BOLD))

        val lore = description.map { line ->
            if (line.contains('<') && line.contains('>')) {
                com.carinaschoppe.skylife.utility.messages.Messages.parse(line)
            } else {
                Component.text(line, NamedTextColor.GRAY)
            }
        }.toMutableList()

        if (selected) {
            lore.add(Component.empty())
            lore.add(Component.text("✔ SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD))
            meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true)
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS)
        } else {
            lore.add(Component.empty())
            lore.add(Component.text("Click to select", NamedTextColor.YELLOW))
        }

        meta.lore(lore)
        item.itemMeta = meta

        return item
    }
}

/**
 * Categories for skills.
 */
enum class SkillType {
    COMBAT,
    DEFENSE,
    MOBILITY,
    SUSTAIN,
    UTILITY,
    SUMMONER
}
