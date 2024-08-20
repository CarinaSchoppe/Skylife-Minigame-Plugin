package com.carinaschoppe.skylife.game.miscellaneous

import com.carinaschoppe.skylife.game.management.Game
import com.carinaschoppe.skylife.utility.messages.Messages
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File

object Utility {


    lateinit var mainLocation: Location


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


    fun endingMatchMessage(game: Game) {
        if (game.livingPlayers.size == 1) {
            game.livingPlayers.forEach {
                it.sendMessage(Messages.PLAYER_WON(game.livingPlayers[0].name))
            }
            game.spectators.forEach {
                it.sendMessage(Messages.PLAYER_WON(game.livingPlayers[0].name))
            }
        }

        //TODO: game over message
        game.livingPlayers.forEach {
            it.sendMessage(Messages.GAME_OVER())
        }
        game.spectators.forEach {
            it.sendMessage(Messages.GAME_OVER())
        }

    }

}