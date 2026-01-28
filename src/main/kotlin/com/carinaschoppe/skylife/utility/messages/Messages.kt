package com.carinaschoppe.skylife.utility.messages

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage

object Messages {

    val NAME_COLOR: NamedTextColor = NamedTextColor.GOLD

    val SUCCESS_COLOR: NamedTextColor = NamedTextColor.GOLD
    val MESSAGE_COLOR: NamedTextColor = NamedTextColor.GREEN
    val ERROR_COLOR: NamedTextColor = NamedTextColor.RED
    val GRAY_COLOR: NamedTextColor = NamedTextColor.GRAY
    val ACCENT_COLOR: NamedTextColor = NamedTextColor.AQUA

    val PREFIX: Component = Component.text("[", GRAY_COLOR, TextDecoration.BOLD).append(Component.text("Skylife", ACCENT_COLOR, TextDecoration.BOLD)).append(Component.text("] ", GRAY_COLOR, TextDecoration.BOLD))

    /**
     * Parses MiniMessage into an Adventure Component.
     */
    fun parse(text: String): Component {
        return MINI_MESSAGE.deserialize(text)
    }

    private val MINI_MESSAGE = MiniMessage.miniMessage()

    // Message templates - stored as strings and can be loaded from messages.json
    // Use placeholders like <player>, <game>, <count>, etc.
    var TEMPLATES = Templates()

    // Simple message components that can be serialized to messages.json
    var PLAYER_AMOUNT_SET: Component = PREFIX.append(Component.text("Player amount set", MESSAGE_COLOR, TextDecoration.BOLD))

    var DATABASE_CONNECTED: Component = PREFIX.append(Component.text("The Database was successfully connected!", MESSAGE_COLOR, TextDecoration.BOLD))

    var DATABASE_TABLES_CREATED: Component = PREFIX.append(Component.text("The Database tables where successfully created!", MESSAGE_COLOR, TextDecoration.BOLD))

    fun STATS(own: Boolean, name: String, kills: Int, deaths: Int, wins: Int, games: Int, points: Int, rank: Int): Component {
        return Component.text()
            .append(Component.text("--- Stats of ", MESSAGE_COLOR))
            .append(Component.text(if (own) "You" else name, ACCENT_COLOR, TextDecoration.BOLD))
            .append(Component.text(" ---", MESSAGE_COLOR))
            .append(Component.newline())
            .append(Component.text("Rank: ", MESSAGE_COLOR).append(Component.text("#$rank", ACCENT_COLOR)))
            .append(Component.newline())
            .append(Component.text("Points: ", MESSAGE_COLOR).append(Component.text(points, ACCENT_COLOR)))
            .append(Component.newline())
            .append(Component.text("Kills: ", MESSAGE_COLOR).append(Component.text(kills, ACCENT_COLOR)))
            .append(Component.newline())
            .append(Component.text("Deaths: ", MESSAGE_COLOR).append(Component.text(deaths, ACCENT_COLOR)))
            .append(Component.newline())
            .append(Component.text("Wins: ", MESSAGE_COLOR).append(Component.text(wins, ACCENT_COLOR)))
            .append(Component.newline())
            .append(Component.text("Games: ", MESSAGE_COLOR).append(Component.text(games, ACCENT_COLOR)))
            .build()
    }

    var INGAME_START: Component = PREFIX.append(Component.text("Game started", MESSAGE_COLOR, TextDecoration.BOLD))

    // Dynamic messages that use parameters - keep as functions
    fun GAME_CREATED(gameName: String): Component {
        return PREFIX.append(Component.text("Game ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" created", MESSAGE_COLOR))
    }

    var GAME_SAVED: Component = PREFIX.append(Component.text("Game saved", MESSAGE_COLOR, TextDecoration.BOLD))

    var GAME_DELETED: Component = PREFIX.append(Component.text("Game deleted", MESSAGE_COLOR, TextDecoration.BOLD))

    var GAME_OVER: Component = PREFIX.append(Component.text("The game is over", MESSAGE_COLOR, TextDecoration.BOLD))

    var COUNTDOWN_STOPPED: Component = PREFIX.append(Component.text("Countdown stopped", MESSAGE_COLOR, TextDecoration.BOLD))

    fun COUNTDOWN(seconds: Int): Component {
        return PREFIX.append(Component.text("Game starting in ", MESSAGE_COLOR))
            .append(Component.text(seconds, NAME_COLOR, TextDecoration.BOLD))
            .append(Component.text(" seconds", MESSAGE_COLOR))
    }

    var PROTECTION_ENDED: Component = PREFIX.append(Component.text("Protection has ended!", MESSAGE_COLOR, TextDecoration.BOLD))

    fun PROTECTION_ENDING(seconds: Int): Component {
        return PREFIX.append(Component.text("Protection ending in ", MESSAGE_COLOR))
            .append(Component.text(seconds, NAME_COLOR, TextDecoration.BOLD))
            .append(Component.text(" seconds!", MESSAGE_COLOR))
    }

    fun GAME_END_TIMER(timer: Int): Component {
        return PREFIX.append(Component.text("Game will end in ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(timer, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" seconds", MESSAGE_COLOR))
    }
    fun LOCATION_ADDED(type: String, gameName: String, amount: Int = -1): Component {
        return PREFIX.append(Component.text("Location to game ", MESSAGE_COLOR, TextDecoration.BOLD).append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" and type ", MESSAGE_COLOR, TextDecoration.BOLD)).append(Component.text(type, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" has been added", MESSAGE_COLOR, TextDecoration.BOLD)).append(if (amount != -1) Component.text(" Amount: ", MESSAGE_COLOR, TextDecoration.BOLD).append(Component.text(amount, NAME_COLOR, TextDecoration.BOLD)) else Component.empty()))
    }
    fun PLAYER_JOINS_SERVER(playerName: String): Component {
        return PREFIX.append(Component.text("Welcome ", MESSAGE_COLOR, TextDecoration.BOLD))
            .append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" to the Skylife Server!", MESSAGE_COLOR))
    }

    fun PLAYER_JOINS_GAME(gameName: String): Component {
        return PREFIX.append(Component.text("you joined the game ", MESSAGE_COLOR))
            .append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }

    fun PLAYER_DIED(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" died", MESSAGE_COLOR))
    }

    fun PLAYER_KILLED(playerName: String, killedBy: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" was killed by", MESSAGE_COLOR))
            .append(Component.text(killedBy, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }

    fun PLAYER_WON(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" WON THE ROUND", MESSAGE_COLOR))
    }

    fun PLAYER_JOINED(playerName: String, playerCount: Int, maxPlayers: Int): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" joined ", MESSAGE_COLOR))
            .append(Component.text("($playerCount/$maxPlayers)", NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
    }

    fun PLAYER_LEFT_GAME(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" left the game!", MESSAGE_COLOR))
    }

    fun PLAYER_LEFT_GAME_BROADCAST(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, NAME_COLOR, TextDecoration.BOLD))
            .append(Component.text(" has left the game", MESSAGE_COLOR))
    }

    var OWN_PLAYER_LEFT: Component = PREFIX.append(Component.text("you left the game!", MESSAGE_COLOR))

    var NOT_INGAME: Component = PREFIX.append(Component.text("ERROR: you must be in a game!", ERROR_COLOR))

    var ALLREADY_IN_GAME: Component = PREFIX.append(Component.text("ERROR: You are allready in a game!", ERROR_COLOR))

    var CANT_BREAK_BLOCK: Component = PREFIX.append(Component.text("ERROR: You cant break a block while not beeing in a live game!", ERROR_COLOR))

    var CANT_PLACE_BLOCK: Component = PREFIX.append(Component.text("ERROR: You cant place a block while not beeing in a live game!", ERROR_COLOR))

    var CANT_DAMAGE: Component = PREFIX.append(Component.text("ERROR: You cant cause any damage while not beeing in a live game!", ERROR_COLOR))

    fun PLAYER_MISSING(playerCount: Int, requiredPlayers: Int): Component {
        val missingPlayers = requiredPlayers - playerCount
        return PREFIX.append(Component.text("Missing ", MESSAGE_COLOR, TextDecoration.BOLD))
            .append(Component.text(missingPlayers, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" out of the ", MESSAGE_COLOR))
            .append(Component.text(requiredPlayers, NAME_COLOR, TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.text(" Players required", MESSAGE_COLOR))
    }

    fun PLAYERS_ONLINE(playerCount: Int): Component {
        return PREFIX.append(Component.text("Players online: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(playerCount, NAME_COLOR, TextDecoration.BOLD))
    }

    fun GAME_NOT_EXISTS(gameName: String): Component {
        return PREFIX.append(Component.text("ERROR: The Game: ", ERROR_COLOR, TextDecoration.BOLD))
            .append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" does not exist", ERROR_COLOR, TextDecoration.BOLD))
    }

    fun PLAYERS_REMAINING(playerCount: Int): Component {
        return PREFIX.append(Component.text("Players remaining: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(playerCount, NAME_COLOR, TextDecoration.BOLD))
    }

    fun MAP_NAME(mapName: String): Component {
        return PREFIX.append(Component.text("Map: ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(mapName, NAME_COLOR, TextDecoration.BOLD))
    }

    var TELEPORT: Component = PREFIX.append(Component.text("Teleporting all players", MESSAGE_COLOR, TextDecoration.BOLD))

    fun KIT(kitName: String): Component {
        return PREFIX.append(Component.text("Player uses kit ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(kitName, NAME_COLOR, TextDecoration.BOLD))
    }

    fun LOBBY_TIMER(roundTime: Int): Component {
        return PREFIX.append(Component.text("Round starts in ", MESSAGE_COLOR, TextDecoration.BOLD))
         .append(Component.text(roundTime, NAME_COLOR, TextDecoration.BOLD))
         .append(Component.text(" seconds", MESSAGE_COLOR))
    }

    fun PROTECTION_TIME(protectionTime: Int): Component {
        return PREFIX.append(Component.text("Protection time ends in ", MESSAGE_COLOR, TextDecoration.BOLD))
        .append(Component.text(protectionTime, NAME_COLOR, TextDecoration.BOLD))
        .append(Component.text(" seconds", MESSAGE_COLOR))
    }

    var ROUND_SPEED_ALL: Component = PREFIX.append(Component.text("The Round has been sped up", MESSAGE_COLOR, TextDecoration.BOLD))

    var ROUND_SPEED_LOW: Component = PREFIX.append(Component.text("You canÂ´t speed up the round cause its allready speeded up", MESSAGE_COLOR, TextDecoration.BOLD))

    var PROTECTION_ENDS: Component = PREFIX.append(Component.text("PROTECTION TIME IS OVER, FIGHT!!", MESSAGE_COLOR, TextDecoration.BOLD))

    var ERROR_PERMISSION: Component = PREFIX.append(Component.text("ERROR: You don't have permission to use this command", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_NOTPLAYER: Component = PREFIX.append(Component.text("ERROR: Command must be executed by a player", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_ARGUMENT: Component = PREFIX.append(Component.text("ERROR: Invalid argument", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_NO_GAME: Component = PREFIX.append(Component.text("ERROR: No game found", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_NO_PATTERN: Component = PREFIX.append(Component.text("ERROR: No game pattern found", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_PATTERN: Component = PREFIX.append(Component.text("ERROR: Game pattern already exists", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_PLAYER_NOT_FOUND: Component = PREFIX.append(Component.text("ERROR: Player not found", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_COMMAND: Component = PREFIX.append(Component.text("ERROR: Command failed", ERROR_COLOR, TextDecoration.BOLD))

    var ERROR_GAME_FULL_OR_STARTED: Component = PREFIX.append(Component.text("Spiel nicht gefunden, voll oder bereits gestartet!", ERROR_COLOR, TextDecoration.BOLD))

    fun GAME_PATTERN_NOT_FULLY_DONE(gameName: String): Component {
        return PREFIX.append(Component.text("ERROR: the Game ", ERROR_COLOR, TextDecoration.BOLD).append(Component.text(gameName, NAME_COLOR, TextDecoration.BOLD)).append(Component.text(" is not fully instantiated", ERROR_COLOR, TextDecoration.BOLD)))
    }

    fun KIT_SELECTED(kitName: String): Component {
        return PREFIX.append(Component.text("You have selected the ", MESSAGE_COLOR))
            .append(Component.text(kitName, ACCENT_COLOR, TextDecoration.BOLD))
            .append(Component.text(" kit!", MESSAGE_COLOR))
    }

    // Party System Messages
    var PARTY_CREATED: Component = PREFIX.append(Component.text("Party created! You are now the party leader.", MESSAGE_COLOR))

    var PARTY_ALREADY_IN_PARTY: Component = PREFIX.append(Component.text("You are already in a party!", ERROR_COLOR))

    var PARTY_NOT_IN_PARTY: Component = PREFIX.append(Component.text("You are not in a party!", ERROR_COLOR))

    var PARTY_INVITE_USAGE: Component = PREFIX.append(Component.text("Usage: /party invite <player>", ERROR_COLOR))

    var PARTY_ACCEPT_USAGE: Component = PREFIX.append(Component.text("Usage: /party accept <player>", ERROR_COLOR))

    var PARTY_KICK_USAGE: Component = PREFIX.append(Component.text("Usage: /party kick <player>", ERROR_COLOR))

    var PARTY_PROMOTE_USAGE: Component = PREFIX.append(Component.text("Usage: /party promote <player>", ERROR_COLOR))

    var PARTY_CANNOT_INVITE_SELF: Component = PREFIX.append(Component.text("You cannot invite yourself!", ERROR_COLOR))

    fun PLAYER_NOT_ONLINE(playerName: String): Component {
        return PREFIX.append(Component.text("Player ", ERROR_COLOR))
            .append(Component.text(playerName, NAME_COLOR))
            .append(Component.text(" is not online!", ERROR_COLOR))
    }

    fun PARTY_INVITE_SENT(playerName: String): Component {
        return PREFIX.append(Component.text("Party invite sent to ", MESSAGE_COLOR))
            .append(Component.text(playerName, ACCENT_COLOR))
            .append(Component.text("!", MESSAGE_COLOR))
    }

    fun PARTY_INVITE_RECEIVED(inviterName: String): Component {
        return PREFIX.append(Component.text(inviterName, ACCENT_COLOR, TextDecoration.BOLD))
            .append(Component.text(" has invited you to their party!", MESSAGE_COLOR))
            .append(Component.newline())
            .append(Component.text("Type ", MESSAGE_COLOR))
            .append(Component.text("/party accept $inviterName", ACCENT_COLOR))
            .append(Component.text(" to join!", MESSAGE_COLOR))
    }

    fun PARTY_JOINED(inviterName: String): Component {
        return PREFIX.append(Component.text("You have joined ", MESSAGE_COLOR))
            .append(Component.text(inviterName, ACCENT_COLOR))
            .append(Component.text("'s party!", MESSAGE_COLOR))
    }

    fun PARTY_MEMBER_JOINED(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, ACCENT_COLOR))
            .append(Component.text(" has joined the party!", MESSAGE_COLOR))
    }

    var PARTY_LEFT: Component = PREFIX.append(Component.text("You have left the party.", MESSAGE_COLOR))

    fun PARTY_MEMBER_LEFT(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, ACCENT_COLOR))
            .append(Component.text(" has left the party.", MESSAGE_COLOR))
    }

    var PARTY_PROMOTED_TO_LEADER: Component = PREFIX.append(Component.text("You are now the party leader!", MESSAGE_COLOR, TextDecoration.BOLD))

    fun PARTY_PROMOTED(playerName: String): Component {
        return PREFIX.append(Component.text("You have promoted ", MESSAGE_COLOR))
            .append(Component.text(playerName, ACCENT_COLOR))
            .append(Component.text(" to party leader!", MESSAGE_COLOR))
    }

    fun PARTY_NEW_LEADER(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, ACCENT_COLOR))
            .append(Component.text(" is now the party leader!", MESSAGE_COLOR))
    }

    fun PARTY_KICKED(playerName: String): Component {
        return PREFIX.append(Component.text("You have kicked ", MESSAGE_COLOR))
            .append(Component.text(playerName, ACCENT_COLOR))
            .append(Component.text(" from the party!", MESSAGE_COLOR))
    }

    var PARTY_KICKED_BY_LEADER: Component = PREFIX.append(Component.text("You have been kicked from the party!", ERROR_COLOR))

    fun PARTY_MEMBER_KICKED(playerName: String): Component {
        return PREFIX.append(Component.text(playerName, ACCENT_COLOR))
            .append(Component.text(" has been kicked from the party!", MESSAGE_COLOR))
    }

    var PARTY_LIST_HEADER: Component = PREFIX.append(Component.text("=== Party Members ===", ACCENT_COLOR, TextDecoration.BOLD))

    fun PARTY_LIST_LEADER(leaderName: String): Component {
        return Component.text("  ★ ", NAME_COLOR, TextDecoration.BOLD)
            .append(Component.text(leaderName, ACCENT_COLOR, TextDecoration.BOLD))
            .append(Component.text(" (Leader)", GRAY_COLOR))
    }

    fun PARTY_LIST_MEMBER(memberName: String, status: String): Component {
        return Component.text("  • ", GRAY_COLOR)
            .append(parse(status))
            .append(Component.text(" ", GRAY_COLOR))
            .append(Component.text(memberName, MESSAGE_COLOR))
    }

    fun PARTY_LIST_FOOTER(size: Int): Component {
        return Component.text("Total: ", GRAY_COLOR)
            .append(Component.text("$size", ACCENT_COLOR))
            .append(Component.text(" members", GRAY_COLOR))
    }

    var PARTY_NO_INVITES: Component = PREFIX.append(Component.text("You have no pending party invites.", MESSAGE_COLOR))

    var PARTY_INVITES_HEADER: Component = PREFIX.append(Component.text("=== Pending Party Invites ===", ACCENT_COLOR, TextDecoration.BOLD))

    fun PARTY_INVITE_ENTRY(inviterName: String, secondsLeft: Long): Component {
        return Component.text("  • ", GRAY_COLOR)
            .append(Component.text(inviterName, ACCENT_COLOR))
            .append(Component.text(" (expires in ", GRAY_COLOR))
            .append(Component.text("${secondsLeft}s", MESSAGE_COLOR))
            .append(Component.text(")", GRAY_COLOR))
    }

    var PARTY_ONLY_LEADER_CAN_JOIN: Component = PREFIX.append(Component.text("Only the party leader can join games! The whole party will join together.", ERROR_COLOR))

    var PARTY_HELP_HEADER: Component = PREFIX.append(Component.text("=== Party Commands ===", ACCENT_COLOR, TextDecoration.BOLD))

    var PARTY_HELP_CREATE: Component = Component.text("  /party create", ACCENT_COLOR)
        .append(Component.text(" - Create a new party", GRAY_COLOR))

    var PARTY_HELP_INVITE: Component = Component.text("  /party invite <player>", ACCENT_COLOR)
        .append(Component.text(" - Invite a player to your party", GRAY_COLOR))

    var PARTY_HELP_ACCEPT: Component = Component.text("  /party accept <player>", ACCENT_COLOR)
        .append(Component.text(" - Accept a party invite", GRAY_COLOR))

    var PARTY_HELP_LEAVE: Component = Component.text("  /party leave", ACCENT_COLOR)
        .append(Component.text(" - Leave your current party", GRAY_COLOR))

    var PARTY_HELP_KICK: Component = Component.text("  /party kick <player>", ACCENT_COLOR)
        .append(Component.text(" - Kick a player from your party (leader only)", GRAY_COLOR))

    var PARTY_HELP_PROMOTE: Component = Component.text("  /party promote <player>", ACCENT_COLOR)
        .append(Component.text(" - Promote a player to party leader", GRAY_COLOR))

    var PARTY_HELP_LIST: Component = Component.text("  /party list", ACCENT_COLOR)
        .append(Component.text(" - List all party members", GRAY_COLOR))

    var PARTY_HELP_INVITES: Component = Component.text("  /party invites", ACCENT_COLOR)
        .append(Component.text(" - List all pending invites", GRAY_COLOR))
}




