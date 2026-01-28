package com.carinaschoppe.skylife.events.player

import com.carinaschoppe.skylife.game.GameCluster
import com.carinaschoppe.skylife.game.gamestates.IngameState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

/**
 * Listener to manage food level changes for players.
 *
 * This listener prevents food level changes for players who are not in an active game.
 * This ensures that players in the lobby or other non-game states don't lose hunger,
 * while still allowing hunger mechanics to function normally during gameplay.
 */
class PlayerLoosesSaturationListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        val player = event.entity as? org.bukkit.entity.Player ?: return

        // Only allow food level changes if the player is in an active game
        val game = GameCluster.getGame(player)
        if (game == null || game.currentState !is IngameState) {
            event.isCancelled = true
            player.foodLevel = 20 // Keep food level at maximum
        }
    }
}