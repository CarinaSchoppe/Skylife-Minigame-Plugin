package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.game.skills.SkillManager
import com.carinaschoppe.skylife.game.skills.SlowFallBootsSkill
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object Items {

    val ANTI_FALL_BOOTS: ItemStack = ItemBuilder(Material.NETHERITE_BOOTS).addName(SkillManager.skills[SlowFallBootsSkill::class.java]!!.name).addLore("These Boots allow you to get no Fall-Damage").build()

    val FILLER_PANEL: ItemStack = ItemBuilder(Material.GLASS_PANE).addName("").addEnchantment(Enchantment.LOYALTY, 1).build()

    val LEVEL_PADERBORN: ItemStack = ItemBuilder(Material.IRON_SHOVEL).addName("test").addLore("Minilevel fast Rounds").build()
}