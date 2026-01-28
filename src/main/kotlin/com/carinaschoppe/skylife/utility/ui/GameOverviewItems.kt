package com.carinaschoppe.skylife.utility.ui

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.game.Game
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Shared item helpers for the game overview menu.
 */
object GameOverviewItems {

    private const val MENU_NAME = "Spieleübersicht"
    private const val NEXT_NAME = "Nächste Seite"
    private const val PREVIOUS_NAME = "Vorherige Seite"

    private val menuKey: NamespacedKey by lazy { NamespacedKey(Skylife.instance, "game_overview_menu") }
    private val navKey: NamespacedKey by lazy { NamespacedKey(Skylife.instance, "game_overview_nav") }
    private val gameKey: NamespacedKey by lazy { NamespacedKey(Skylife.instance, "game_overview_game") }

    fun createMenuItem(): ItemStack {
        return ItemBuilder(Material.COMPASS)
            .addName(MENU_NAME)
            .addLore("Liste der Spiele öffnen")
            .modifyMeta { meta ->
                meta.persistentDataContainer[menuKey, PersistentDataType.BYTE] = 1.toByte()
            }
            .build()
    }

    fun isMenuItem(item: ItemStack?): Boolean {
        val meta = item?.itemMeta ?: return false
        return meta.persistentDataContainer.has(menuKey, PersistentDataType.BYTE)
    }

    fun createGameItem(game: Game): ItemStack {
        return ItemBuilder(Material.PAPER)
            .addName(game.mapName)
            .addLore(
                "Spieler: ${game.livingPlayers.size}/${game.maxPlayers}"
            )
            .modifyMeta { meta ->
                meta.persistentDataContainer[gameKey, PersistentDataType.STRING] = game.name
            }
            .build()
    }

    fun getGameName(item: ItemStack?): String? {
        val meta = item?.itemMeta ?: return null
        return meta.persistentDataContainer[gameKey, PersistentDataType.STRING]
    }

    fun createNavItem(action: NavAction): ItemStack {
        val name = if (action == NavAction.NEXT) NEXT_NAME else PREVIOUS_NAME
        return ItemBuilder(Material.ARROW)
            .addName(name)
            .modifyMeta { meta ->
                meta.persistentDataContainer[navKey, PersistentDataType.STRING] = action.name.lowercase()
            }
            .build()
    }

    fun getNavAction(item: ItemStack?): NavAction? {
        val meta = item?.itemMeta ?: return null
        val value = meta.persistentDataContainer[navKey, PersistentDataType.STRING] ?: return null
        return when (value.lowercase()) {
            "next" -> NavAction.NEXT
            "previous" -> NavAction.PREVIOUS
            else -> null
        }
    }
}
