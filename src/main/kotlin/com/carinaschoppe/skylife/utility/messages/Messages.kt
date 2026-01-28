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

    /**
     * Parses a template string with placeholder replacements.
     * Replaces <prefix> tag with the actual prefix component.
     * Supports any placeholder tags like <player>, <game>, <count>, etc.
     */
    private fun parseTemplate(template: String, vararg placeholders: Pair<String, Any>): Component {
        // First replace <prefix> with serialized PREFIX
        val prefixSerialized = MINI_MESSAGE.serialize(PREFIX)
        var processed = template.replace("<prefix>", prefixSerialized)

        // Replace all other placeholders
        placeholders.forEach { (key, value) ->
            processed = processed.replace("<$key>", value.toString())
        }

        return MINI_MESSAGE.deserialize(processed)
    }

    private val MINI_MESSAGE = MiniMessage.miniMessage()

    // Message templates - stored as strings and can be loaded from messages.json
    // Use placeholders like <player>, <game>, <count>, etc.
    var TEMPLATES = Templates()

    // ===== Message Properties and Functions =====
    // These use the TEMPLATES system and can be fully customized via messages.json

    // Database messages
    val DATABASE_CONNECTED: Component get() = parseTemplate(TEMPLATES.databaseConnected)
    val DATABASE_TABLES_CREATED: Component get() = parseTemplate(TEMPLATES.databaseTablesCreated)

    // Game setup messages
    val PLAYER_AMOUNT_SET: Component get() = parseTemplate(TEMPLATES.playerAmountSet)
    fun GAME_CREATED(gameName: String): Component = parseTemplate(TEMPLATES.gameCreated, "game" to gameName)
    val GAME_SAVED: Component get() = parseTemplate(TEMPLATES.gameSaved)
    val GAME_DELETED: Component get() = parseTemplate(TEMPLATES.gameDeleted)
    fun LOCATION_ADDED(type: String, gameName: String, amount: Int = -1): Component {
        return if (amount != -1) {
            parseTemplate(TEMPLATES.locationAddedWithAmount, "game" to gameName, "type" to type, "amount" to amount)
        } else {
            parseTemplate(TEMPLATES.locationAdded, "game" to gameName, "type" to type)
        }
    }

    // Game state messages
    val INGAME_START: Component get() = parseTemplate(TEMPLATES.ingameStart)
    val GAME_OVER: Component get() = parseTemplate(TEMPLATES.gameOver)
    val COUNTDOWN_STOPPED: Component get() = parseTemplate(TEMPLATES.countdownStopped)
    fun COUNTDOWN(seconds: Int): Component = parseTemplate(TEMPLATES.countdown, "seconds" to seconds)
    val PROTECTION_ENDED: Component get() = parseTemplate(TEMPLATES.protectionEnded)
    fun PROTECTION_ENDING(seconds: Int): Component = parseTemplate(TEMPLATES.protectionEnding, "seconds" to seconds)
    fun GAME_END_TIMER(timer: Int): Component = parseTemplate(TEMPLATES.gameEndTimer, "timer" to timer)
    val ROUND_SPEED_ALL: Component get() = parseTemplate(TEMPLATES.roundSpeedAll)
    val ROUND_SPEED_LOW: Component get() = parseTemplate(TEMPLATES.roundSpeedLow)
    val PROTECTION_ENDS: Component get() = parseTemplate(TEMPLATES.protectionEnds)

    // Player join/leave messages
    fun PLAYER_JOINS_SERVER(playerName: String): Component = parseTemplate(TEMPLATES.playerJoinsServer, "player" to playerName)
    fun PLAYER_JOINS_GAME(gameName: String): Component = parseTemplate(TEMPLATES.playerJoinsGame, "game" to gameName)
    fun PLAYER_JOINED(playerName: String, playerCount: Int, maxPlayers: Int): Component =
        parseTemplate(TEMPLATES.playerJoined, "player" to playerName, "playerCount" to playerCount, "maxPlayers" to maxPlayers)

    fun PLAYER_LEFT_GAME(playerName: String): Component = parseTemplate(TEMPLATES.playerLeftGame, "player" to playerName)
    fun PLAYER_LEFT_GAME_BROADCAST(playerName: String): Component = parseTemplate(TEMPLATES.playerLeftGameBroadcast, "player" to playerName)
    val OWN_PLAYER_LEFT: Component get() = parseTemplate(TEMPLATES.ownPlayerLeft)

    // Player death messages
    fun PLAYER_DIED(playerName: String): Component = parseTemplate(TEMPLATES.playerDied, "player" to playerName)
    fun PLAYER_KILLED(playerName: String, killedBy: String): Component =
        parseTemplate(TEMPLATES.playerKilled, "player" to playerName, "killer" to killedBy)

    fun PLAYER_WON(playerName: String): Component = parseTemplate(TEMPLATES.playerWon, "player" to playerName)

    // Player count messages
    fun PLAYER_MISSING(playerCount: Int, requiredPlayers: Int): Component {
        val missingPlayers = requiredPlayers - playerCount
        return parseTemplate(TEMPLATES.playerMissing, "missing" to missingPlayers, "required" to requiredPlayers)
    }

    fun PLAYERS_ONLINE(playerCount: Int): Component = parseTemplate(TEMPLATES.playersOnline, "count" to playerCount)
    fun PLAYERS_REMAINING(playerCount: Int): Component = parseTemplate(TEMPLATES.playersRemaining, "count" to playerCount)

    // Game info messages
    fun MAP_NAME(mapName: String): Component = parseTemplate(TEMPLATES.mapName, "map" to mapName)
    val TELEPORT: Component get() = parseTemplate(TEMPLATES.teleport)
    fun LOBBY_TIMER(roundTime: Int): Component = parseTemplate(TEMPLATES.lobbyTimer, "time" to roundTime)
    fun PROTECTION_TIME(protectionTime: Int): Component = parseTemplate(TEMPLATES.protectionTime, "time" to protectionTime)

    // Kit messages
    fun KIT(kitName: String): Component = parseTemplate(TEMPLATES.kit, "kit" to kitName)
    fun KIT_SELECTED(kitName: String): Component = parseTemplate(TEMPLATES.kitSelected, "kit" to kitName)

    // Stats messages
    fun STATS(own: Boolean, name: String, kills: Int, deaths: Int, wins: Int, games: Int, points: Int, rank: Int): Component {
        val displayName = if (own) "You" else name
        return Component.text()
            .append(parseTemplate(TEMPLATES.statsHeader, "name" to displayName))
            .append(Component.newline())
            .append(parseTemplate(TEMPLATES.statsRank, "rank" to rank))
            .append(Component.newline())
            .append(parseTemplate(TEMPLATES.statsPoints, "points" to points))
            .append(Component.newline())
            .append(parseTemplate(TEMPLATES.statsKills, "kills" to kills))
            .append(Component.newline())
            .append(parseTemplate(TEMPLATES.statsDeaths, "deaths" to deaths))
            .append(Component.newline())
            .append(parseTemplate(TEMPLATES.statsWins, "wins" to wins))
            .append(Component.newline())
            .append(parseTemplate(TEMPLATES.statsGames, "games" to games))
            .build()
    }

    // Error messages - game related
    val NOT_INGAME: Component get() = parseTemplate(TEMPLATES.notIngame)
    val ALLREADY_IN_GAME: Component get() = parseTemplate(TEMPLATES.alreadyInGame)
    val CANT_BREAK_BLOCK: Component get() = parseTemplate(TEMPLATES.cantBreakBlock)
    val CANT_PLACE_BLOCK: Component get() = parseTemplate(TEMPLATES.cantPlaceBlock)
    val CANT_DAMAGE: Component get() = parseTemplate(TEMPLATES.cantDamage)
    val ERROR_GAME_FULL_OR_STARTED: Component get() = parseTemplate(TEMPLATES.errorGameFullOrStarted)
    fun GAME_NOT_EXISTS(gameName: String): Component = parseTemplate(TEMPLATES.gameNotExists, "game" to gameName)
    fun GAME_PATTERN_NOT_FULLY_DONE(gameName: String): Component =
        parseTemplate(TEMPLATES.gamePatternNotFullyDone, "game" to gameName)

    val ERROR_NO_GAME: Component get() = parseTemplate(TEMPLATES.errorNoGame)
    val ERROR_NO_PATTERN: Component get() = parseTemplate(TEMPLATES.errorNoPattern)
    val ERROR_PATTERN: Component get() = parseTemplate(TEMPLATES.errorPattern)

    // Error messages - permission/command related
    val ERROR_PERMISSION: Component get() = parseTemplate(TEMPLATES.errorPermission)
    val ERROR_NOTPLAYER: Component get() = parseTemplate(TEMPLATES.errorNotPlayer)
    val ERROR_ARGUMENT: Component get() = parseTemplate(TEMPLATES.errorArgument)
    val ERROR_PLAYER_NOT_FOUND: Component get() = parseTemplate(TEMPLATES.errorPlayerNotFound)
    val ERROR_COMMAND: Component get() = parseTemplate(TEMPLATES.errorCommand)
    fun PLAYER_NOT_ONLINE(playerName: String): Component = parseTemplate(TEMPLATES.playerNotOnline, "player" to playerName)

    // Party messages
    val PARTY_CREATED: Component get() = parseTemplate(TEMPLATES.partyCreated)
    val PARTY_ALREADY_IN_PARTY: Component get() = parseTemplate(TEMPLATES.partyAlreadyInParty)
    val PARTY_NOT_IN_PARTY: Component get() = parseTemplate(TEMPLATES.partyNotInParty)
    val PARTY_INVITE_USAGE: Component get() = parseTemplate(TEMPLATES.partyInviteUsage)
    val PARTY_ACCEPT_USAGE: Component get() = parseTemplate(TEMPLATES.partyAcceptUsage)
    val PARTY_KICK_USAGE: Component get() = parseTemplate(TEMPLATES.partyKickUsage)
    val PARTY_PROMOTE_USAGE: Component get() = parseTemplate(TEMPLATES.partyPromoteUsage)
    val PARTY_CANNOT_INVITE_SELF: Component get() = parseTemplate(TEMPLATES.partyCannotInviteSelf)
    fun PARTY_INVITE_SENT(playerName: String): Component = parseTemplate(TEMPLATES.partyInviteSent, "player" to playerName)
    fun PARTY_INVITE_RECEIVED(inviterName: String): Component =
        parseTemplate(TEMPLATES.partyInviteReceived, "inviter" to inviterName)

    fun PARTY_JOINED(inviterName: String): Component = parseTemplate(TEMPLATES.partyJoined, "inviter" to inviterName)
    fun PARTY_MEMBER_JOINED(playerName: String): Component = parseTemplate(TEMPLATES.partyMemberJoined, "player" to playerName)
    val PARTY_LEFT: Component get() = parseTemplate(TEMPLATES.partyLeft)
    fun PARTY_MEMBER_LEFT(playerName: String): Component = parseTemplate(TEMPLATES.partyMemberLeft, "player" to playerName)
    val PARTY_PROMOTED_TO_LEADER: Component get() = parseTemplate(TEMPLATES.partyPromotedToLeader)
    fun PARTY_PROMOTED(playerName: String): Component = parseTemplate(TEMPLATES.partyPromoted, "player" to playerName)
    fun PARTY_NEW_LEADER(playerName: String): Component = parseTemplate(TEMPLATES.partyNewLeader, "player" to playerName)
    fun PARTY_KICKED(playerName: String): Component = parseTemplate(TEMPLATES.partyKicked, "player" to playerName)
    val PARTY_KICKED_BY_LEADER: Component get() = parseTemplate(TEMPLATES.partyKickedByLeader)
    fun PARTY_MEMBER_KICKED(playerName: String): Component = parseTemplate(TEMPLATES.partyMemberKicked, "player" to playerName)
    val PARTY_LIST_HEADER: Component get() = parseTemplate(TEMPLATES.partyListHeader)
    fun PARTY_LIST_LEADER(leaderName: String): Component = parseTemplate(TEMPLATES.partyListLeader, "leader" to leaderName)
    fun PARTY_LIST_MEMBER(memberName: String, status: String): Component =
        parseTemplate(TEMPLATES.partyListMember, "member" to memberName, "status" to status)

    fun PARTY_LIST_FOOTER(size: Int): Component = parseTemplate(TEMPLATES.partyListFooter, "size" to size)
    val PARTY_NO_INVITES: Component get() = parseTemplate(TEMPLATES.partyNoInvites)
    val PARTY_INVITES_HEADER: Component get() = parseTemplate(TEMPLATES.partyInvitesHeader)
    fun PARTY_INVITE_ENTRY(inviterName: String, secondsLeft: Long): Component =
        parseTemplate(TEMPLATES.partyInviteEntry, "inviter" to inviterName, "seconds" to secondsLeft)

    val PARTY_ONLY_LEADER_CAN_JOIN: Component get() = parseTemplate(TEMPLATES.partyOnlyLeaderCanJoin)
    val PARTY_HELP_HEADER: Component get() = parseTemplate(TEMPLATES.partyHelpHeader)
    val PARTY_HELP_CREATE: Component get() = parseTemplate(TEMPLATES.partyHelpCreate)
    val PARTY_HELP_INVITE: Component get() = parseTemplate(TEMPLATES.partyHelpInvite)
    val PARTY_HELP_ACCEPT: Component get() = parseTemplate(TEMPLATES.partyHelpAccept)
    val PARTY_HELP_LEAVE: Component get() = parseTemplate(TEMPLATES.partyHelpLeave)
    val PARTY_HELP_KICK: Component get() = parseTemplate(TEMPLATES.partyHelpKick)
    val PARTY_HELP_PROMOTE: Component get() = parseTemplate(TEMPLATES.partyHelpPromote)
    val PARTY_HELP_LIST: Component get() = parseTemplate(TEMPLATES.partyHelpList)
    val PARTY_HELP_INVITES: Component get() = parseTemplate(TEMPLATES.partyHelpInvites)
}




