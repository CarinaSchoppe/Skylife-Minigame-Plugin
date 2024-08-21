package com.carinaschoppe.skylife.utility.messages

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

object Messages {

    //TODO: ADD PREFIX TO EVERY MESSAGE [Skylife]

    val NAME_COLOR: NamedTextColor = NamedTextColor.GOLD
    val MESSAGE_COLOR: NamedTextColor = NamedTextColor.GREEN
    val ERROR_COLOR: NamedTextColor = NamedTextColor.RED
    val GRAY_COLOR: NamedTextColor = NamedTextColor.GRAY

    val PLAYER_AMOUNT_SET = fun(): Component {
        return Component.text("Player amount set", MESSAGE_COLOR, TextDecoration.BOLD)
    }

    val PREFIX: Component = Component.text("[", GRAY_COLOR, TextDecoration.BOLD).append(Component.text("Skylife", NamedTextColor.AQUA, TextDecoration.BOLD)).append(Component.text("] ", GRAY_COLOR, TextDecoration.BOLD))

    val STATS = fun(kills: Int, deaths: Int, wins: Int, games: Int): Component {
        return Component.text("Kills: ", MESSAGE_COLOR, TextDecoration.BOLD)
        .append(Component.text(kills, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" | Deaths: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(deaths, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" | Wins: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(wins, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" | Games: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(games, NAME_COLOR, TextDecoration.BOLD))
    }

    val INGAME_START = fun(): Component {
        return Component.text("Game started", MESSAGE_COLOR, TextDecoration.BOLD)
    }

    val GAME_CREATED = fun(gameName: String): Component {
        return Component.text("Game ", MESSAGE_COLOR, TextDecoration.BOLD)
        .append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" created", MESSAGE_COLOR))
    }
    val GAME_SAVED = fun(): Component {
        return Component.text("Game saved", MESSAGE_COLOR, TextDecoration.BOLD)
    }
    val GAME_DELETED = fun(): Component {
        return Component.text("Game deleted", MESSAGE_COLOR, TextDecoration.BOLD)
    }
    val GAME_OVER = fun(): Component {
        return Component.text("The game is over", MESSAGE_COLOR, TextDecoration.BOLD)
    }
    val LOCATION_ADDED = fun(): Component {
        return Component.text("Location added", MESSAGE_COLOR, TextDecoration.BOLD)
    }
    val PLAYER_JOINS_GAME = fun(playerName: String): Component {
        return Component.text("Welcome ", MESSAGE_COLOR, TextDecoration.BOLD)
            .append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" to the Skylife Server!", MESSAGE_COLOR))
    }
    val PLAYER_DIED = fun(playerName: String): Component {
        return (Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" died", MESSAGE_COLOR))
    }
    val PLAYER_KILLED = fun(playerName: String, killedBy: String): Component {
        return (Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" was killed by", MESSAGE_COLOR))
            .append(Component.text(killedBy, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }
    val PLAYER_WON = fun(playerName: String): Component {
        return (Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" WON THE ROUND", MESSAGE_COLOR))
    }
    val PLAYER_JOINED = fun(playerName: String): Component {
        return (Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" joined", MESSAGE_COLOR))
    }

    val PLAYERS_ONLINE = fun(playerCount: Int): Component {
        return Component.text("Players online: ", MESSAGE_COLOR, TextDecoration.BOLD)
        .append(Component.text(playerCount, NAME_COLOR, TextDecoration.BOLD))
    }
    val PLAYERS_REMAINING = fun(playerCount: Int): Component {
        return Component.text("Players remaining: ", MESSAGE_COLOR, TextDecoration.BOLD)
        .append(Component.text(playerCount, NAME_COLOR, TextDecoration.BOLD))
    }

    val MAP_NAME = fun(mapName: String): Component {
        return Component.text("Map: ", MESSAGE_COLOR, TextDecoration.BOLD)
        .append(Component.text(mapName, NAME_COLOR, TextDecoration.BOLD))
    }

    val TELEPORT = fun(): Component {
        return Component.text("Teleporting all players", MESSAGE_COLOR, TextDecoration.BOLD)
    }
    val KIT = fun(kitName: String): Component {
        return Component.text("Player uses kit ", MESSAGE_COLOR, TextDecoration.BOLD)
        .append(Component.text(kitName, NAME_COLOR, TextDecoration.BOLD))
    }

    val ROUND_STARTS = fun(roundTime: Int): Component {
        return Component.text("Round starts in ", MESSAGE_COLOR, TextDecoration.BOLD)
         .append(Component.text(roundTime, NAME_COLOR, TextDecoration.BOLD))
         .append(Component.text(" seconds", MESSAGE_COLOR))
    }
    val PROTECTION_TIME = fun(protectionTime: Int): Component {
        return Component.text("Protection time ends in ", MESSAGE_COLOR, TextDecoration.BOLD)
        .append(Component.text(protectionTime, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" seconds", MESSAGE_COLOR))
    }

    //TODO: roundTimer message till game force shuts

    //TODO: player jons needs Player ... joins [amount current / amount total]

    //TODO: message kit xxx equipped
    //TODO: leave message
    //TODO: Missing x players to be able to start the game
    //TODO the game has been speeded up. (cause of /start)


    val PROTECTION_ENDS = fun(): Component {
        return Component.text("PROTECTION TIME IS OVER, FIGHT!!", MESSAGE_COLOR, TextDecoration.BOLD)
    }

    val ERROR_PERMISSION = fun(): Component {
        return Component.text("ERROR: You don't have permission to use this command", ERROR_COLOR, TextDecoration.BOLD)
    }
    val ERROR_NOTPLAYER = fun(): Component {
        return Component.text("ERROR: Command must be executed by a player", ERROR_COLOR, TextDecoration.BOLD)
    }
    val ERROR_ARGUMENT = fun(): Component {
        return Component.text("ERROR: Invalid argument", ERROR_COLOR, TextDecoration.BOLD)
    }
    val ERROR_NOGAME = fun(): Component {
        return Component.text("ERROR: No game found", ERROR_COLOR, TextDecoration.BOLD)
    }
    val ERROR_NOPATTERN = fun(): Component {
        return Component.text("ERROR: No game pattern found", ERROR_COLOR, TextDecoration.BOLD)
    }
    val ERROR_PATTERN = fun(): Component {
        return Component.text("ERROR: Game pattern already exists", ERROR_COLOR, TextDecoration.BOLD)
    }
    val ERROR_PLAYER_NOT_FOUND = fun(): Component {
        return Component.text("ERROR: Player not found", ERROR_COLOR, TextDecoration.BOLD)
    }
    val ERROR_COMMAND = fun(): Component {
        return Component.text("ERROR: Command failed", ERROR_COLOR, TextDecoration.BOLD)
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


