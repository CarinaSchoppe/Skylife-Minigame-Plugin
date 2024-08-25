package com.carinaschoppe.skylife.game.miscellaneous

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.game.management.gamestates.IngameState
import com.carinaschoppe.skylife.game.skills.Kit
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File

object Utility {


    lateinit var mainLocation: Location


    //TODO: Kits
    val playerKits = mutableMapOf<Player, Kit>()

    //TODO: items
    val chestItems = mutableSetOf<ItemStack>()


    fun locationWorldConverter(location: Location, game: Game): Location {
        return Location(Bukkit.getWorld(game.gameID.toString()), location.x, location.y, location.z, location.yaw, location.pitch)
    }

    fun unloadWorld(game: Game): Boolean {
        val world = Bukkit.getWorld(game.gameID.toString()) ?: return false
        // Stelle sicher, dass alle Spieler aus der Welt teleportiert werden, bevor du sie entlädst
        // Entlade die Welt
        Bukkit.unloadWorld(world, false)

        val folder = File(Bukkit.getServer().worldContainer, world.name)
        if (folder.exists()) {
            return folder.deleteRecursively()
        }
        return false
    }


    fun checkGameOver(game: Game): Boolean {
        if (game.currentState !is IngameState) return false


        if (game.livingPlayers.size > 2) {
            return false
        }

        if (game.livingPlayers.isNullOrEmpty()) {
            return false
        }

        //TODO: fehlt da was?
        game.currentState.stop()
        return true
    }


    fun endingMatchMessage(game: Game) {
        game.livingPlayers.forEach {
            if (game.livingPlayers.size == 1)
                it.sendMessage(Messages.instance.PLAYER_WON(game.livingPlayers[0].name))
            it.sendMessage(Messages.instance.GAME_OVER())
        }
        game.spectators.forEach {
            if (game.livingPlayers.size == 1)
                it.sendMessage(Messages.instance.PLAYER_WON(game.livingPlayers[0].name))
            it.sendMessage(Messages.instance.GAME_OVER())
        }


    }

}