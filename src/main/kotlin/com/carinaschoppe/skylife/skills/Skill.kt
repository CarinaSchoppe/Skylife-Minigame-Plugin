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
    val type: SkillType,
    val rarity: SkillRarity
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
        SkillType.SUSTAIN,
        SkillRarity.COMMON
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
        SkillType.SUSTAIN,
        SkillRarity.RARE
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
        SkillType.DEFENSE,
        SkillRarity.RARE
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
        SkillType.MOBILITY,
        SkillRarity.COMMON
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
        SkillType.UTILITY,
        SkillRarity.COMMON
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
        SkillType.UTILITY,
        SkillRarity.RARE
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
        SkillType.SUMMONER,
        SkillRarity.EPIC
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
        SkillType.MOBILITY,
        SkillRarity.EPIC
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
        SkillType.UTILITY,
        SkillRarity.RARE
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
        SkillType.UTILITY,
        SkillRarity.COMMON
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
        SkillType.COMBAT,
        SkillRarity.RARE
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
        SkillType.COMBAT,
        SkillRarity.EPIC
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
        SkillType.UTILITY,
        SkillRarity.LEGENDARY
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
        SkillType.COMBAT,
        SkillRarity.LEGENDARY
    ),

    KLETTERER(
        "Kletterer",
        listOf(
            "Climb up walls by looking at them",
            "and moving forward.",
            "",
            "<gray>Type: <yellow>Mobility</yellow></gray>"
        ),
        Material.LADDER,
        SkillType.MOBILITY,
        SkillRarity.RARE
    ),

    KANGAROO(
        "Kangaroo",
        listOf(
            "Perform a double jump to reach",
            "higher places.",
            "",
            "<gray>Type: <yellow>Mobility</yellow></gray>"
        ),
        Material.RABBIT_FOOT,
        SkillType.MOBILITY,
        SkillRarity.EPIC
    ),

    NINJA(
        "Ninja",
        listOf(
            "Become invisible for a short time.",
            "Use strategically for ambushes.",
            "",
            "<gray>Type: <yellow>Stealth</yellow></gray>"
        ),
        Material.GRAY_DYE,
        SkillType.UTILITY,
        SkillRarity.EPIC
    ),

    PILOT(
        "Pilot",
        listOf(
            "Fly for 3 seconds, then suffer",
            "3 seconds of nausea.",
            "",
            "<gray>Type: <yellow>Mobility</yellow></gray>"
        ),
        Material.ELYTRA,
        SkillType.MOBILITY,
        SkillRarity.LEGENDARY
    ),

    GOD(
        "God",
        listOf(
            "Receive a golden apple every",
            "90 seconds for extra sustain.",
            "",
            "<gray>Type: <yellow>Sustain / Defense</yellow></gray>"
        ),
        Material.GOLDEN_APPLE,
        SkillType.SUSTAIN,
        SkillRarity.LEGENDARY
    ),

    KUNG_FU_MASTER(
        "Kung Fu Master",
        listOf(
            "Start with a knockback stick",
            "to push enemies away.",
            "",
            "<gray>Type: <yellow>Combat / Control</yellow></gray>"
        ),
        Material.STICK,
        SkillType.COMBAT,
        SkillRarity.COMMON
    ),

    RITTER(
        "Ritter",
        listOf(
            "Begin the battle with full",
            "diamond armor protection.",
            "",
            "<gray>Type: <yellow>Defense</yellow></gray>"
        ),
        Material.DIAMOND_CHESTPLATE,
        SkillType.DEFENSE,
        SkillRarity.RARE
    );

    /**
     * Creates an ItemStack representing this skill.
     * @param selected Whether this skill is currently selected
     * @param unlocked Whether this skill is unlocked by the player
     */
    fun toItemStack(selected: Boolean, unlocked: Boolean = true): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta

        // Title with rarity color
        meta.displayName(
            Component.text(displayName, rarity.color, TextDecoration.BOLD)
        )

        val lore = description.map { line ->
            if (line.contains('<') && line.contains('>')) {
                com.carinaschoppe.skylife.utility.messages.Messages.parse(line)
            } else {
                Component.text(line, NamedTextColor.GRAY)
            }
        }.toMutableList()

        // Add rarity information
        lore.add(Component.empty())
        lore.add(
            Component.text("Rarity: ", NamedTextColor.GRAY)
                .append(rarity.getColoredName())
        )

        if (!unlocked) {
            // Show locked status and price
            lore.add(Component.empty())
            lore.add(Component.text("🔒 LOCKED", NamedTextColor.RED, TextDecoration.BOLD))
            if (rarity.price > 0) {
                lore.add(Component.text("Price: ${rarity.price} coins", NamedTextColor.YELLOW))
                lore.add(Component.empty())
                lore.add(Component.text("Click to purchase", NamedTextColor.GREEN))
            }
        } else if (selected) {
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
