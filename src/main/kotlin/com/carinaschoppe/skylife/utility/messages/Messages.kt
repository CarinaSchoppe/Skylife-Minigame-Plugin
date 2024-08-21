package com.carinaschoppe.skylife.utility.messages

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

object Messages {
    private val namecolor: NamedTextColor = NamedTextColor.GOLD
    private val messagecolor: NamedTextColor = NamedTextColor.AQUA
    private val errorcolor: NamedTextColor = NamedTextColor.RED


    val GAME_CREATED = fun(): Component {
        return Component.text("Game created", messagecolor, TextDecoration.BOLD)
    }
    val GAME_SAVED = fun(): Component {
        return Component.text("Game saved", messagecolor, TextDecoration.BOLD)
    }
    val GAME_DELETED = fun(): Component {
        return Component.text("Game deleted", messagecolor, TextDecoration.BOLD)
    }
    val GAME_OVER = fun(): Component {
        return Component.text("The game is over", messagecolor, TextDecoration.BOLD)
    }
    val LOCATION_ADDED = fun(): Component {
        return Component.text("Location added", messagecolor, TextDecoration.BOLD)
    }
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
    val PLAYER_JOINED = fun(playerName: String): Component {
        return (Component.text(playerName, namecolor, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" joined", messagecolor))
    }

    val PLAYERS_ONLINE = fun(playerCount: Int): Component {
        return Component.text("Players online: $playerCount", messagecolor, TextDecoration.BOLD)
    }
    val PLAYERS_REMAINING = fun(playerCount: Int): Component {
        return Component.text("Players remaining: $playerCount", messagecolor, TextDecoration.BOLD)
    }

    val MAP_NAME = fun(mapName: String): Component {
        return Component.text("Map: $mapName", messagecolor, TextDecoration.BOLD)
    }

    val TELEPORT = fun(): Component {
        return Component.text("Teleporting all players", messagecolor, TextDecoration.BOLD)
    }
    val KIT = fun(kitName: String): Component {
        return Component.text("Player uses kit $kitName", messagecolor, TextDecoration.BOLD)
    }

    val ROUND_STARTS = fun(roundTime: Int): Component {
        return Component.text("Round starts in $roundTime seconds", messagecolor, TextDecoration.BOLD)
    }
    val PROTECTION_TIME = fun(protectionTime: Int): Component {
        return Component.text("Protection time ends in $protectionTime seconds", messagecolor, TextDecoration.BOLD)
    }

    //TODO: roundTimer message till game force shuts

    //TODO: player jons needs Player ... joins [amount current / amount total]

    //TODO: message kit xxx equipped
    //TODO: leave message
    //TODO: Missing x players to be able to start the game
    //TODO the game has been speeded up. (cause of /start)


    val PROTECTION_ENDS = fun(): Component {
        return Component.text("PROTECTION TIME IS OVER, FIGHT!!", messagecolor, TextDecoration.BOLD)
    }

    val ERROR_PERMISSION = fun(): Component {
        return Component.text("ERROR: You don't have permission to use this command", errorcolor, TextDecoration.BOLD)
    }
    val ERROR_NOTPLAYER = fun(): Component {
        return Component.text("ERROR: Command must be executed by a player", errorcolor, TextDecoration.BOLD)
    }
    val ERROR_ARGUMENT = fun(): Component {
        return Component.text("ERROR: Invalid argument", errorcolor, TextDecoration.BOLD)
    }
    val ERROR_NOGAME = fun(): Component {
        return Component.text("ERROR: No game found", errorcolor, TextDecoration.BOLD)
    }
    val ERROR_NOPATTERN = fun(): Component {
        return Component.text("ERROR: No game pattern found", errorcolor, TextDecoration.BOLD)
    }
    val ERROR_PATTERN = fun(): Component {
        return Component.text("ERROR: Game pattern already exists", errorcolor, TextDecoration.BOLD)
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


