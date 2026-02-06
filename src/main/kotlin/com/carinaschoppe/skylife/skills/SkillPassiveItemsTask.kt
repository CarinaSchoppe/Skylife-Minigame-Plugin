package com.carinaschoppe.skylife.skills

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionType

/**
 * Repeating task that gives passive items to players with specific skills.
 * Runs every 10 seconds and checks for:
 * - Snow Spammer: 1 snowball
 * - Endermaster: 1 ender pearl
 * - Witch: 1 random potion
 * - God: 1 golden apple every 90 seconds
 */
object SkillPassiveItemsTask {

    private var taskId: Int = -1
    private val godSkillCounters = mutableMapOf<java.util.UUID, Int>()

    /**
     * Starts the repeating task.
     * @param plugin The plugin instance
     */
    fun start(plugin: Plugin) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            Bukkit.getOnlinePlayers().forEach { player ->
                handlePlayer(player)
            }
        }, 200L, 200L) // 200 ticks = 10 seconds
    }

    /**
     * Stops the repeating task.
     */
    fun stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId)
            taskId = -1
        }
        godSkillCounters.clear()
    }

    /**
     * Resets the GOD skill counter for a specific player.
     * Should be called when a game starts.
     */
    fun resetGodCounter(playerUUID: java.util.UUID) {
        godSkillCounters.remove(playerUUID)
    }

    /**
     * Resets all GOD skill counters.
     * Should be called when games end.
     */
    fun resetAllGodCounters() {
        godSkillCounters.clear()
    }

    private fun handlePlayer(player: org.bukkit.entity.Player) {
        val game = GameCluster.getGame(player) ?: return
        if (game.currentState !is IngameState) return
        if (game.spectators.contains(player)) return

        SkillsManager.getActiveSkills(player).forEach { skill ->
            applySkillPassiveItem(player, skill)
        }
    }

    private fun applySkillPassiveItem(player: org.bukkit.entity.Player, skill: Skill) {
        when (skill) {
            Skill.SNOW_SPAMMER -> giveItem(player, Material.SNOWBALL)
            Skill.ENDERMASTER -> giveItem(player, Material.ENDER_PEARL)
            Skill.WITCH -> player.inventory.addItem(createRandomPotion())
            Skill.GOD -> handleGodSkill(player)
            else -> return
        }
    }

    private fun giveItem(player: org.bukkit.entity.Player, material: Material) {
        player.inventory.addItem(ItemStack(material, 1))
    }

    private fun handleGodSkill(player: org.bukkit.entity.Player) {
        val counter = godSkillCounters.getOrDefault(player.uniqueId, 0) + 1

        if (counter >= 9) {
            giveItem(player, Material.GOLDEN_APPLE)
            godSkillCounters[player.uniqueId] = 0
        } else {
            godSkillCounters[player.uniqueId] = counter
        }
    }

    /**
     * Creates a random potion (splash or drinkable).
     */
    private fun createRandomPotion(): ItemStack {
        val potionTypes = listOf(
            PotionType.SWIFTNESS,
            PotionType.STRENGTH,
            PotionType.HEALING,
            PotionType.LEAPING,
            PotionType.REGENERATION,
            PotionType.FIRE_RESISTANCE,
            PotionType.WATER_BREATHING,
            PotionType.INVISIBILITY,
            PotionType.NIGHT_VISION,
            PotionType.WEAKNESS,
            PotionType.POISON,
            PotionType.SLOWNESS,
            PotionType.MUNDANE
        )

        val type = potionTypes.random()
        val isSplash = (0..1).random() == 1

        val material = if (isSplash) Material.SPLASH_POTION else Material.POTION
        val potion = ItemStack(material)

        val meta = potion.itemMeta as? PotionMeta ?: return potion
        meta.basePotionType = type
        potion.itemMeta = meta

        return potion
    }
}
