package com.carinaschoppe.skylife.skills

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Manages the application and removal of skill effects for players.
 */
object SkillEffectsManager {

    /**
     * Applies all active skill effects to a player.
     * Called when the game starts.
     */
    fun applySkillEffects(player: Player) {
        val skills = SkillsManager.getActiveSkills(player)

        skills.forEach { skill ->
            when (skill) {
                Skill.JUMBO -> applyJumbo(player)
                Skill.REGENERATOR -> applyRegenerator(player)
                Skill.ABSORBER -> applyAbsorber(player)
                Skill.FEATHERFALL -> {} // Handled by listener
                Skill.SNOW_SPAMMER -> {} // Handled by task
                Skill.LUCKY_BIRD -> applyLuckyBird(player)
                Skill.WOLFLORD -> applyWolflord(player)
                Skill.ENDERMASTER -> {} // Handled by task
                Skill.WITCH -> {} // Handled by task
                Skill.BUILDER -> applyBuilder(player)
                Skill.SWORDMASTER -> applySwordmaster(player)
                Skill.BOWMASTER -> applyBowmaster(player)
                Skill.INVISIBLE_STALKER -> {} // Handled by listener
                Skill.STRENGTH_CORE -> applyStrengthCore(player)
                Skill.CLIMBER -> {} // Handled by listener
                Skill.KANGAROO -> {} // Handled by listener
                Skill.NINJA -> applyNinja(player)
                Skill.PILOT -> applyPilot(player)
                Skill.GOD -> {} // Handled by task
                Skill.KUNG_FU_MASTER -> applyKungFuMaster(player)
                Skill.KNIGHT -> applyKnight(player)
            }
        }
    }

    /**
     * Removes all skill effects from a player.
     * Called when the game ends.
     */
    fun removeSkillEffects(player: Player) {
        // Remove all potion effects
        player.activePotionEffects.forEach { effect ->
            player.removePotionEffect(effect.type)
        }

        // Reset food level
        player.foodLevel = 20
        player.saturation = 5f
    }

    private fun applyJumbo(player: Player) {
        player.foodLevel = 20
        player.saturation = 20f
    }

    private fun applyRegenerator(player: Player) {
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.REGENERATION,
                Int.MAX_VALUE,
                0,
                false,
                false,
                false
            )
        )
    }

    private fun applyAbsorber(player: Player) {
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.ABSORPTION,
                Int.MAX_VALUE,
                0,
                false,
                false,
                false
            )
        )
    }

    private fun applyLuckyBird(player: Player) {
        // Give lucky egg
        val egg = ItemStack(Material.EGG, 1)
        val meta = egg.itemMeta
        meta.displayName(net.kyori.adventure.text.Component.text("Lucky Egg", net.kyori.adventure.text.format.NamedTextColor.GOLD))
        meta.lore(
            listOf(
                net.kyori.adventure.text.Component.text("Throw for a random effect!", net.kyori.adventure.text.format.NamedTextColor.GRAY)
            )
        )
        egg.itemMeta = meta
        player.inventory.addItem(egg)
    }

    private fun applyWolflord(player: Player) {
        // Give 4-6 stacks of bones
        val stacks = (4..6).random()
        val bones = ItemStack(Material.BONE, 64)
        for (i in 0 until stacks) {
            player.inventory.addItem(bones.clone())
        }
    }

    private fun applyBuilder(player: Player) {
        // Give 64 stone bricks
        val blocks = ItemStack(Material.STONE_BRICKS, 64)
        player.inventory.addItem(blocks)
    }

    private fun applySwordmaster(player: Player) {
        // Give iron sword with sharpness I
        val sword = ItemStack(Material.IRON_SWORD)
        sword.addEnchantment(Enchantment.SHARPNESS, 1)

        // Give shield
        val shield = ItemStack(Material.SHIELD)

        player.inventory.addItem(sword)
        player.inventory.addItem(shield)
    }

    private fun applyBowmaster(player: Player) {
        // Give bow with infinity and power I
        val bow = ItemStack(Material.BOW)
        bow.addEnchantment(Enchantment.INFINITY, 1)
        bow.addEnchantment(Enchantment.POWER, 1)

        // Give arrows
        val arrows = ItemStack(Material.ARROW, 64)

        player.inventory.addItem(bow)
        player.inventory.addItem(arrows)
    }

    private fun applyStrengthCore(player: Player) {
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.STRENGTH,
                Int.MAX_VALUE,
                0,
                false,
                false,
                false
            )
        )
    }

    private fun applyNinja(player: Player) {
        // Give gray dye as activation item
        val ninjaItem = ItemStack(Material.GRAY_DYE, 3)
        val meta = ninjaItem.itemMeta
        meta.displayName(net.kyori.adventure.text.Component.text("Ninja Cloak", net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY))
        meta.lore(
            listOf(
                net.kyori.adventure.text.Component.text("Right-click to become invisible", net.kyori.adventure.text.format.NamedTextColor.GRAY),
                net.kyori.adventure.text.Component.text("for 5 seconds!", net.kyori.adventure.text.format.NamedTextColor.GRAY)
            )
        )
        ninjaItem.itemMeta = meta
        player.inventory.addItem(ninjaItem)
    }

    private fun applyPilot(player: Player) {
        // Give elytra as activation item (not wearable, just for activation)
        val pilotItem = ItemStack(Material.ELYTRA, 1)
        val meta = pilotItem.itemMeta
        meta.displayName(net.kyori.adventure.text.Component.text("Pilot Wings", net.kyori.adventure.text.format.NamedTextColor.AQUA))
        meta.lore(
            listOf(
                net.kyori.adventure.text.Component.text("Right-click to fly for 3 seconds,", net.kyori.adventure.text.format.NamedTextColor.GRAY),
                net.kyori.adventure.text.Component.text("then suffer nausea!", net.kyori.adventure.text.format.NamedTextColor.GRAY)
            )
        )
        pilotItem.itemMeta = meta
        player.inventory.addItem(pilotItem)
    }

    private fun applyKungFuMaster(player: Player) {
        // Give knockback stick
        val stick = ItemStack(Material.STICK, 1)
        stick.addEnchantment(Enchantment.KNOCKBACK, 1)

        val meta = stick.itemMeta
        meta.displayName(net.kyori.adventure.text.Component.text("Kung Fu Stick", net.kyori.adventure.text.format.NamedTextColor.GOLD))
        meta.lore(
            listOf(
                net.kyori.adventure.text.Component.text("Push your enemies away!", net.kyori.adventure.text.format.NamedTextColor.GRAY)
            )
        )
        stick.itemMeta = meta

        player.inventory.addItem(stick)
    }

    private fun applyKnight(player: Player) {
        // Give full diamond armor
        val helmet = ItemStack(Material.DIAMOND_HELMET)
        val chestplate = ItemStack(Material.DIAMOND_CHESTPLATE)
        val leggings = ItemStack(Material.DIAMOND_LEGGINGS)
        val boots = ItemStack(Material.DIAMOND_BOOTS)

        player.inventory.helmet = helmet
        player.inventory.chestplate = chestplate
        player.inventory.leggings = leggings
        player.inventory.boots = boots
    }
}
