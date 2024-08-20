package com.carinaschoppe.skylife.utility.messages

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

object Messages {
    private val namecolor: NamedTextColor = NamedTextColor.GOLD
    private val messagecolor: NamedTextColor = NamedTextColor.AQUA


    val PLAYER_JOINS_GAME = fun(playerName: String): Component {
        return Component.text("Welcome ", messagecolor, TextDecoration.BOLD)
            .append(Component.text(playerName, namecolor, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" to the Skylife Server!", messagecolor))
    }
    val PLAYER_DIED = fun(playerName: String): Component {
        return (Component.text(playerName, namecolor, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" died", messagecolor))
    }
    val PLAYER_KILLED = fun(playerName: String, killedBy: String): Component {
        return (Component.text(playerName, namecolor, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" was killed by", messagecolor))
            .append(Component.text(killedBy, namecolor, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }
    val PLAYER_WON = fun(playerName: String): Component {
        return (Component.text(playerName, namecolor, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" WON THE ROUND", messagecolor))
    }

}
//NUMBER REMAINING PLAYERS
//PLAYERNAME was killed by PLAYERNAME
//PLAYER died
//PLAYER joined
//PLAYERNAME WON THE ROUND
//nicht ide nötigen rechte
//befehl muss von einem spieler ausgeführt werden
//es fehlen argumente fuer diesen befehl
//Players online: PLAYERNUMBER

//MAP: MAPNAME
//round starts in 5
//round starts in 4
//round starts in 3
//round starts in 2
//round starts in 1
//TELEPORTING ALL PLAYERS
//PLAYER uses kit KIT
//round starts in 3
//round starts in 2
//round starts in 1
//protection time ends in 30 seconds
//protection time ends in 10 seconds
//protection time ends in 5 seconds
//protection time ends in 3 seconds
//protection time ends in 2 seconds
//protection time ends in 1 seconds
//PROTECTION TIME IS OVER, FIGHT!!


