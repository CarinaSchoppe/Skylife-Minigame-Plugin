package com.carinaschoppe.skylife.utility.messages

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

class Messages {

    companion object {
        lateinit var instance: Messages
    }

    val NAME_COLOR: NamedTextColor = NamedTextColor.GOLD
    val MESSAGE_COLOR: NamedTextColor = NamedTextColor.GREEN
    val ERROR_COLOR: NamedTextColor = NamedTextColor.RED
    val GRAY_COLOR: NamedTextColor = NamedTextColor.GRAY

     val PREFIX: Component = Component.text("[", GRAY_COLOR, TextDecoration.BOLD).append(Component.text("Skylife", NamedTextColor.AQUA, TextDecoration.BOLD)).append(Component.text("] ", GRAY_COLOR, TextDecoration.BOLD))

    val PLAYER_AMOUNT_SET = fun(): Component {
        return PREFIX.append(Component.text("Player amount set", MESSAGE_COLOR, TextDecoration.BOLD))
    }

    val DATABASE_CONNECTED = PREFIX.append(Component.text("The Database was successfully connected!", MESSAGE_COLOR, TextDecoration.BOLD))

    val DATABASE_TABLES_CREATED = PREFIX.append(Component.text("The Database tables where successfully created!", MESSAGE_COLOR, TextDecoration.BOLD))

    val STATS = fun(own: Boolean, kills: Int, deaths: Int, wins: Int, games: Int, name: String?): Component {
        return PREFIX.append(if (own) Component.text("Your stats: ", MESSAGE_COLOR, TextDecoration.BOLD) else Component.text("The stats of $name: ", MESSAGE_COLOR, TextDecoration.BOLD)).append(Component.text("Kills: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(kills, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" | Deaths: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(deaths, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" | Wins: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(wins, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" | Games: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(games, NAME_COLOR, TextDecoration.BOLD))
    }

    val INGAME_START = fun(): Component {
        return PREFIX.append(Component.text("Game started", MESSAGE_COLOR, TextDecoration.BOLD))
    }

    val GAME_CREATED = fun(gameName: String): Component {
        return PREFIX.append(Component.text("Game ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" created", MESSAGE_COLOR))
    }
    val GAME_SAVED = fun(): Component {
        return PREFIX.append(Component.text("Game saved", MESSAGE_COLOR, TextDecoration.BOLD))
    }
    val GAME_DELETED = fun(): Component {
        return PREFIX.append(Component.text("Game deleted", MESSAGE_COLOR, TextDecoration.BOLD))
    }
    val GAME_OVER = fun(): Component {
        return PREFIX.append(Component.text("The game is over", MESSAGE_COLOR, TextDecoration.BOLD))
    }
    val GAME_END_TIMER = fun(timer: Int): Component {
        return PREFIX.append(Component.text("Game will end in ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(timer, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" seconds", MESSAGE_COLOR))
    }
    fun LOCATION_ADDED(type: String, gameName: String, amount: Int = -1): Component {
        return PREFIX.append(Component.text("Location to game ", MESSAGE_COLOR, TextDecoration.BOLD).append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" and type ", MESSAGE_COLOR, TextDecoration.BOLD)).append(Component.text(type, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" has been added", MESSAGE_COLOR, TextDecoration.BOLD)).append(if (amount != -1) Component.text(" Amount: ", MESSAGE_COLOR, TextDecoration.BOLD).append(Component.text(amount, NAME_COLOR, TextDecoration.BOLD)) else Component.empty()))
    }
    val PLAYER_JOINS_SERVER = fun(playerName: String): Component {
        return PREFIX.append(Component.text("Welcome ", MESSAGE_COLOR, TextDecoration.BOLD))
            .append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" to the Skylife Server!", MESSAGE_COLOR))
    }
    val PLAYER_JOINS_GAME = fun(gameName: String): Component {
        return PREFIX.append(Component.text("you joined the game ", MESSAGE_COLOR))
            .append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }
    val PLAYER_DIED = fun(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" died", MESSAGE_COLOR))
    }
    val PLAYER_KILLED = fun(playerName: String, killedBy: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" was killed by", MESSAGE_COLOR))
            .append(Component.text(killedBy, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }
    val PLAYER_WON = fun(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" WON THE ROUND", MESSAGE_COLOR))
    }
    val PLAYER_JOINED = fun(playerName: String, playerCount: Int, maxPlayers: Int): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" joined ", MESSAGE_COLOR))
            .append(Component.text("($playerCount/$maxPlayers)", NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }
    val PLAYER_LEFT = fun(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" left the game!", MESSAGE_COLOR))
    }

    val OWN_PLAYER_LEFT = fun(): Component {
        return PREFIX
            .append(Component.text("you left the game!", MESSAGE_COLOR))
    }

    val ALLREADY_IN_GAME = fun(): Component {
        return PREFIX
            .append(Component.text("ERROR: You are allready in a game!", ERROR_COLOR))
    }

    val CANT_BREAK_BLOCK = fun(): Component {
        return PREFIX
            .append(Component.text("ERROR: You cant break a block while not beeing in a live game!", ERROR_COLOR))
    }

    val CANT_PLACE_BLOCK = fun(): Component {
        return PREFIX
            .append(Component.text("ERROR: You cant place a block while not beeing in a live game!", ERROR_COLOR))
    }
    val CANT_DAMAGE = fun(): Component {
        return PREFIX
            .append(Component.text("ERROR: You cant cause any damage while not beeing in a live game!", ERROR_COLOR))
    }
    val PLAYER_MISSING = fun(playerCount: Int, requiredPlayers: Int): Component {
        val missingPlayers = requiredPlayers - playerCount
        return PREFIX.append(Component.text("Missing ", MESSAGE_COLOR, TextDecoration.BOLD))
            .append(Component.text(missingPlayers, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" out of the ", MESSAGE_COLOR))
            .append(Component.text(requiredPlayers, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" Players required", MESSAGE_COLOR))
    }
    val PLAYERS_ONLINE = fun(playerCount: Int): Component {
        return PREFIX.append(Component.text("Players online: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(playerCount, NAME_COLOR, TextDecoration.BOLD))
    }

    val GAME_NOT_EXISTS = fun(gameName: String): Component {
        return PREFIX.append(Component.text("ERROR: The Game: ", ERROR_COLOR, TextDecoration.BOLD))
            .append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" does not exist", ERROR_COLOR, TextDecoration.BOLD))
    }

    val PLAYERS_REMAINING = fun(playerCount: Int): Component {
        return PREFIX.append(Component.text("Players remaining: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(playerCount, NAME_COLOR, TextDecoration.BOLD))
    }

    val MAP_NAME = fun(mapName: String): Component {
        return PREFIX.append(Component.text("Map: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(mapName, NAME_COLOR, TextDecoration.BOLD))
    }

    val TELEPORT = fun(): Component {
        return PREFIX.append(Component.text("Teleporting all players", MESSAGE_COLOR, TextDecoration.BOLD))
    }
    val KIT = fun(kitName: String): Component {
        return PREFIX.append(Component.text("Player uses kit ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(kitName, NAME_COLOR, TextDecoration.BOLD))
    }

    val LOBBY_TIMER = fun(roundTime: Int): Component {
        return PREFIX.append(Component.text("Round starts in ", MESSAGE_COLOR, TextDecoration.BOLD))
         .append(Component.text(roundTime, NAME_COLOR, TextDecoration.BOLD))
         .append(Component.text(" seconds", MESSAGE_COLOR))
    }
    val PROTECTION_TIME = fun(protectionTime: Int): Component {
        return PREFIX.append(Component.text("Protection time ends in ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(protectionTime, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" seconds", MESSAGE_COLOR))
    }
    val ROUND_SPEED_ALL = fun(): Component {
        return PREFIX.append(Component.text("The Round has been sped up", MESSAGE_COLOR, TextDecoration.BOLD))
    }

    val ROUND_SPEED_LOW = fun(): Component {
        return PREFIX.append(Component.text("You can´t speed up the round cause its allready speeded up", MESSAGE_COLOR, TextDecoration.BOLD))
    }

    val PROTECTION_ENDS = fun(): Component {
        return PREFIX.append(Component.text("PROTECTION TIME IS OVER, FIGHT!!", MESSAGE_COLOR, TextDecoration.BOLD))
    }

    val ERROR_PERMISSION = fun(): Component {
        return PREFIX.append(Component.text("ERROR: You don't have permission to use this command", ERROR_COLOR, TextDecoration.BOLD))
    }
    val ERROR_NOTPLAYER = fun(): Component {
        return PREFIX.append(Component.text("ERROR: Command must be executed by a player", ERROR_COLOR, TextDecoration.BOLD))
    }
    val ERROR_ARGUMENT = fun(): Component {
        return PREFIX.append(Component.text("ERROR: Invalid argument", ERROR_COLOR, TextDecoration.BOLD))
    }
    val ERROR_NO_GAME = fun(): Component {
        return PREFIX.append(Component.text("ERROR: No game found", ERROR_COLOR, TextDecoration.BOLD))
    }
    val ERROR_NO_PATTERN = fun(): Component {
        return PREFIX.append(Component.text("ERROR: No game pattern found", ERROR_COLOR, TextDecoration.BOLD))
    }
    val ERROR_PATTERN = fun(): Component {
        return PREFIX.append(Component.text("ERROR: Game pattern already exists", ERROR_COLOR, TextDecoration.BOLD))
    }
    val ERROR_PLAYER_NOT_FOUND = fun(): Component {
        return PREFIX.append(Component.text("ERROR: Player not found", ERROR_COLOR, TextDecoration.BOLD))
    }
    val ERROR_COMMAND = fun(): Component {
        return PREFIX.append(Component.text("ERROR: Command failed", ERROR_COLOR, TextDecoration.BOLD))
    }

    val GAME_PATTERN_NOT_FULLY_DONE = fun(gameName: String): Component {
        return PREFIX.append(Component.text("ERROR: the Game ", ERROR_COLOR, TextDecoration.BOLD).append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" is not fully instantiated", ERROR_COLOR, TextDecoration.BOLD)))
    }
}

