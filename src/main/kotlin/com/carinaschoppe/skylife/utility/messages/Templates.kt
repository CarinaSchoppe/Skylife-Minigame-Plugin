package com.carinaschoppe.skylife.utility.messages

/**
 * Message templates that can be serialized to and loaded from messages.json.
 * Templates use MiniMessage format with placeholders: <player>, <game>, <count>, etc.
 *
 * Example: "<gray>[<aqua><bold>Skylife</bold></aqua>] <green>Welcome <gold><bold><player></bold></gold> to the server!"
 *
 * Note: Using object instead of data class to avoid JVM parameter limit (255).
 */
object Templates {
    private const val GUI_CLICK_TO_DECREASE = "<gray>Click to decrease</gray>"
    private const val GUI_CLICK_TO_INCREASE = "<gray>Click to increase</gray>"
    private const val GUI_CLICK_TO_SET_LOCATION = "<gray>Click to set your current location</gray>"

    // Prefix
    var prefix: String = "<gray><bold>[</bold></gray><aqua><bold>Skylife</bold></aqua><gray><bold>] </bold></gray>"

    // Database messages
    var databaseConnected: String = "<prefix><green><bold>The Database was successfully connected!</bold></green>"
    var databaseTablesCreated: String = "<prefix><green><bold>The Database tables where successfully created!</bold></green>"

    // Game setup messages
    var playerAmountSet: String = "<prefix><green><bold>Player amount set</bold></green>"
    var gameCreated: String = "<prefix><green><bold>Game </bold></green><gold><bold><game></bold></gold><green> created</green>"
    var gameSaved: String = "<prefix><green><bold>Game saved</bold></green>"
    var gameDeleted: String = "<prefix><green><bold>Game deleted</bold></green>"
    var locationAdded: String = "<prefix><green><bold>Location to game </bold></green><gold><bold><game></bold></gold><green><bold> and type </bold></green><gold><bold><type></bold></gold><green><bold> has been added</bold></green>"
    var locationAddedWithAmount: String = "<prefix><green><bold>Location to game </bold></green><gold><bold><game></bold></gold><green><bold> and type </bold></green><gold><bold><type></bold></gold><green><bold> has been added Amount: </bold></green><gold><bold><amount></bold></gold>"

    // Game state messages
    var ingameStart: String = "<prefix><green><bold>Game started</bold></green>"
    var gameOver: String = "<prefix><green><bold>The game is over</bold></green>"
    var countdownStopped: String = "<prefix><green><bold>Countdown stopped</bold></green>"
    var countdown: String = "<prefix><green>Game starting in </green><gold><bold><seconds></bold></gold><green> seconds</green>"
    var protectionEnded: String = "<prefix><green><bold>Protection has ended!</bold></green>"
    var protectionEnding: String = "<prefix><green>Protection ending in </green><gold><bold><seconds></bold></gold><green> seconds!</green>"
    var protectionEnds: String = "<prefix><green><bold>PROTECTION TIME IS OVER, FIGHT!!</bold></green>"
    var gameEndTimer: String = "<prefix><green><bold>Game will end in </bold></green><gold><bold><timer></bold></gold><green> seconds</green>"
    var roundSpeedAll: String = "<prefix><green><bold>The Round has been sped up</bold></green>"
    var roundSpeedLow: String = "<prefix><green><bold>You canÂ´t speed up the round cause its allready speeded up</bold></green>"

    // Player join/leave messages
    var playerJoinsServer: String = "<prefix><green><bold>Welcome </bold></green><gold><bold><underlined><player></underlined></bold></gold><green> to the Skylife Server!</green>"
    var playerJoinsGame: String = "<prefix><green>you joined the game </green><gold><bold><underlined><game></underlined></bold></gold>"
    var playerJoined: String = "<prefix><gold><bold><underlined><player></underlined></bold></gold><green> joined </green><gold><bold><underlined>(<playerCount>/<maxPlayers>)</underlined></bold></gold>"
    var playerLeftGame: String = "<prefix><gold><bold><underlined><player></underlined></bold></gold><green> left the game!</green>"
    var playerLeftGameBroadcast: String = "<prefix><gold><bold><player></bold></gold><green> has left the game</green>"
    var ownPlayerLeft: String = "<prefix><green>you left the game!</green>"

    // Player death messages
    var playerDied: String = "<prefix><gold><bold><underlined><player></underlined></bold></gold><green> died</green>"
    var playerKilled: String = "<prefix><gold><bold><underlined><player></underlined></bold></gold><green> was killed by</green><gold><bold><underlined><killer></underlined></bold></gold>"
    var playerWon: String = "<prefix><gold><bold><underlined><player></underlined></bold></gold><green> WON THE ROUND</green>"

    // Player count messages
    var playerMissing: String = "<prefix><green><bold>Missing </bold></green><gold><bold><underlined><missing></underlined></bold></gold><green> out of the </green><gold><bold><underlined><required></underlined></bold></gold><green> Players required</green>"
    var playersOnline: String = "<prefix><green><bold>Players online: </bold></green><gold><bold><count></bold></gold>"
    var playersRemaining: String = "<prefix><green><bold>Players remaining: </bold></green><gold><bold><count></bold></gold>"

    // Game info messages
    var mapName: String = "<prefix><green><bold>Map: </bold></green><gold><bold><map></bold></gold>"
    var teleport: String = "<prefix><green><bold>Teleporting all players</bold></green>"
    var lobbyTimer: String = "<prefix><green><bold>Round starts in </bold></green><gold><bold><time></bold></gold><green> seconds</green>"
    var protectionTime: String = "<prefix><green><bold>Protection time ends in </bold></green><gold><bold><time></bold></gold><green> seconds</green>"

    // Skill messages
    var skillSelected: String = "<prefix><green>Skill </green><gold><bold><skill></bold></gold><green> selected!</green>"
    var skillUnselected: String = "<prefix><green>Skill </green><gold><bold><skill></bold></gold><green> unselected!</green>"
    var skillPurchased: String = "<prefix><green>Successfully purchased </green><gold><bold><skill></bold></gold><green> for </green><gold><bold><price> coins</bold></gold><green>!</green>"
    var skillAlreadyOwned: String = "<prefix><red>You already own this skill!</red>"
    var skillNotUnlocked: String = "<prefix><red>You must unlock this skill before you can select it!</red>"
    var skillMaxReached: String = "<prefix><red>You already have <max> skills selected. Unselect one first.</red>"
    var skillInsufficientFunds: String = "<prefix><red>Insufficient coins! Need <price>, have <current></red>"

    // Economy messages
    var coinsEarnedGame: String = "<prefix><green>You earned </green><gold><bold><amount> coins</bold></gold><green> for playing!</green>"
    var coinsEarnedKill: String = "<prefix><green>+</green><gold><bold><amount> coins</bold></gold><green> for a kill!</green>"
    var coinsEarnedWin: String = "<prefix><green>You earned </green><gold><bold><amount> coins</bold></gold><green> for winning!</green>"

    // Admin coin commands
    var coinsGiveUsage: String = "<prefix><red>Usage: /givecoins <player> <amount></red>"
    var coinsRemoveUsage: String = "<prefix><red>Usage: /removecoins <player> <amount></red>"
    var coinsInvalidAmount: String = "<prefix><red>Invalid amount! Please enter a positive number.</red>"
    var coinsGiven: String = "<prefix><green>Gave </green><gold><bold><amount> coins</bold></gold><green> to </green><aqua><player></aqua><green>. New balance: </green><gold><balance></gold>"
    var coinsRemoved: String = "<prefix><green>Removed </green><gold><bold><amount> coins</bold></gold><green> from </green><aqua><player></aqua><green>. New balance: </green><gold><balance></gold>"
    var coinsReceived: String = "<prefix><green>You received </green><gold><bold><amount> coins</bold></gold><green> from an admin!</green>"
    var coinsDeducted: String = "<prefix><red>An admin removed </red><gold><bold><amount> coins</bold></gold><red> from your account.</red>"
    var coinsInsufficient: String = "<prefix><red><player> only has </red><gold><current> coins</gold><red>, cannot remove </red><gold><amount> coins</gold><red>. Removed all coins instead.</red>"

    // Stats message
    var statsHeader: String = "<green>--- Stats of </green><aqua><bold><name></bold></aqua><green> ---</green>"
    var statsRank: String = "<green>Rank: </green><aqua>#<rank></aqua>"
    var statsPoints: String = "<green>Points: </green><aqua><points></aqua>"
    var statsKills: String = "<green>Kills: </green><aqua><kills></aqua>"
    var statsDeaths: String = "<green>Deaths: </green><aqua><deaths></aqua>"
    var statsWins: String = "<green>Wins: </green><aqua><wins></aqua>"
    var statsGames: String = "<green>Games: </green><aqua><games></aqua>"

    // Error messages - game related
    var notIngame: String = "<prefix><red>ERROR: you must be in a game!</red>"
    var alreadyInGame: String = "<prefix><red>ERROR: You are allready in a game!</red>"
    var cantBreakBlock: String = "<prefix><red>ERROR: You cant break a block while not beeing in a live game!</red>"
    var cantPlaceBlock: String = "<prefix><red>ERROR: You cant place a block while not beeing in a live game!</red>"
    var cantDamage: String = "<prefix><red>ERROR: You cant cause any damage while not beeing in a live game!</red>"
    var errorGameFullOrStarted: String = "<prefix><red><bold>Spiel nicht gefunden, voll oder bereits gestartet!</bold></red>"
    var gameNotExists: String = "<prefix><red><bold>ERROR: The Game: </bold></red><gold><bold><game></bold></gold><red><bold> does not exist</bold></red>"
    var gamePatternNotFullyDone: String = "<prefix><red><bold>ERROR: the Game </bold></red><gold><bold><game></bold></gold><red><bold> is not fully instantiated</bold></red>"
    var errorNoGame: String = "<prefix><red><bold>ERROR: No game found</bold></red>"
    var errorNoPattern: String = "<prefix><red><bold>ERROR: No game pattern found</bold></red>"
    var errorPattern: String = "<prefix><red><bold>ERROR: Game pattern already exists</bold></red>"

    // Error messages - permission/command related
    var errorPermission: String = "<prefix><red><bold>ERROR: You don't have permission to use this command</bold></red>"
    var errorNotPlayer: String = "<prefix><red><bold>ERROR: Command must be executed by a player</bold></red>"
    var errorArgument: String = "<prefix><red><bold>ERROR: Invalid argument</bold></red>"
    var errorPlayerNotFound: String = "<prefix><red><bold>ERROR: Player not found</bold></red>"
    var errorCommand: String = "<prefix><red><bold>ERROR: Command failed</bold></red>"
    var playerNotOnline: String = "<prefix><red>Player </red><gold><player></gold><red> is not online!</red>"

    // Party messages
    var partyCreated: String = "<prefix><green>Party created! You are now the party leader.</green>"
    var partyAlreadyInParty: String = "<prefix><red>You are already in a party!</red>"
    var partyNotInParty: String = "<prefix><red>You are not in a party!</red>"
    var partyInviteUsage: String = "<prefix><red>Usage: /party invite <player></red>"
    var partyAcceptUsage: String = "<prefix><red>Usage: /party accept <player></red>"
    var partyKickUsage: String = "<prefix><red>Usage: /party kick <player></red>"
    var partyPromoteUsage: String = "<prefix><red>Usage: /party promote <player></red>"
    var partyCannotInviteSelf: String = "<prefix><red>You cannot invite yourself!</red>"
    var partyInviteSent: String = "<prefix><green>Party invite sent to </green><aqua><player></aqua><green>!</green>"
    var partyInviteReceived: String = "<prefix><aqua><bold><inviter></bold></aqua><green> has invited you to their party!\nType </green><aqua>/party accept <inviter></aqua><green> to join!</green>"
    var partyJoined: String = "<prefix><green>You have joined </green><aqua><inviter></aqua><green>'s party!</green>"
    var partyMemberJoined: String = "<prefix><aqua><player></aqua><green> has joined the party!</green>"
    var partyLeft: String = "<prefix><green>You have left the party.</green>"
    var partyMemberLeft: String = "<prefix><aqua><player></aqua><green> has left the party.</green>"
    var partyPromotedToLeader: String = "<prefix><green><bold>You are now the party leader!</bold></green>"
    var partyPromoted: String = "<prefix><green>You have promoted </green><aqua><player></aqua><green> to party leader!</green>"
    var partyNewLeader: String = "<prefix><aqua><player></aqua><green> is now the party leader!</green>"
    var partyKicked: String = "<prefix><green>You have kicked </green><aqua><player></aqua><green> from the party!</green>"
    var partyKickedByLeader: String = "<prefix><red>You have been kicked from the party!</red>"
    var partyMemberKicked: String = "<prefix><aqua><player></aqua><green> has been kicked from the party!</green>"
    var partyListHeader: String = "<prefix><aqua><bold>=== Party Members ===</bold></aqua>"
    var partyListLeader: String = "  <gold><bold>★ </bold></gold><aqua><bold><leader></bold></aqua><gray> (Leader)</gray>"
    var partyListMember: String = "  <gray>• <status> <member></gray>"
    var partyListFooter: String = "<gray>Total: </gray><aqua><size></aqua><gray> members</gray>"
    var partyNoInvites: String = "<prefix><green>You have no pending party invites.</green>"
    var partyInvitesHeader: String = "<prefix><aqua><bold>=== Pending Party Invites ===</bold></aqua>"
    var partyInviteEntry: String = "  <gray>• </gray><aqua><inviter></aqua><gray> (expires in </gray><green><seconds>s</green><gray>)</gray>"
    var partyOnlyLeaderCanJoin: String = "<prefix><red>Only the party leader can join games! The whole party will join together.</red>"
    var partyHelpHeader: String = "<prefix><aqua><bold>=== Party Commands ===</bold></aqua>"
    var partyHelpCreate: String = "  <aqua>/party create</aqua><gray> - Create a new party</gray>"
    var partyHelpInvite: String = "  <aqua>/party invite <player></aqua><gray> - Invite a player to your party</gray>"
    var partyHelpAccept: String = "  <aqua>/party accept <player></aqua><gray> - Accept a party invite</gray>"
    var partyHelpLeave: String = "  <aqua>/party leave</aqua><gray> - Leave your current party</gray>"
    var partyHelpKick: String = "  <aqua>/party kick <player></aqua><gray> - Kick a player from your party (leader only)</gray>"
    var partyHelpPromote: String = "  <aqua>/party promote <player></aqua><gray> - Promote a player to party leader</gray>"
    var partyHelpList: String = "  <aqua>/party list</aqua><gray> - List all party members</gray>"
    var partyHelpInvites: String = "  <aqua>/party invites</aqua><gray> - List all pending invites</gray>"
    var partyLeaderDisconnected: String = "<prefix><yellow><oldLeader> has disconnected. <newLeader> is now the party leader!</yellow>"

    // Guild messages
    var guildCreateUsage: String = "<prefix><red>Usage: /guild create <name> <tag></red>"
    var guildCreated: String = "<prefix><green>Guild </green><gold><bold><name></bold></gold><green> created with tag </green><aqua><bold>[<tag>]</bold></aqua>"
    var guildInviteUsage: String = "<prefix><red>Usage: /guild invite <player></red>"
    var guildNotInGuild: String = "<prefix><red>You are not in a guild</red>"
    var guildInvited: String = "<prefix><gold><bold><player></bold></gold><green> has been invited to the guild</green>"
    var guildInviteReceived: String = "<prefix><green>You have been invited to guild </green><gold><bold><guild></bold></gold>"
    var guildKickUsage: String = "<prefix><red>Usage: /guild kick <player></red>"
    var guildKicked: String = "<prefix><gold><bold><player></bold></gold><green> has been kicked from the guild</green>"
    var guildKickedSelf: String = "<prefix><red>You have been kicked from the guild</red>"
    var guildPromoteUsage: String = "<prefix><red>Usage: /guild promote <player></red>"
    var guildNotFound: String = "<prefix><red>Guild not found</red>"
    var guildPromoted: String = "<prefix><gold><bold><player></bold></gold><green> has been promoted to </green><aqua><bold><role></bold></aqua>"
    var guildLeft: String = "<prefix><green>You have left the guild </green><gold><bold><guild></bold></gold>"
    var guildMemberLeft: String = "<prefix><gold><bold><player></bold></gold><green> has left the guild</green>"
    var guildFriendlyFireToggled: String = "<prefix><green>Friendly fire has been </green><status>"
    var guildInfoHeader: String = "<aqua><bold>--- Guild Info ---</bold></aqua>"
    var guildInfoName: String = "<green>Name: </green><gold><bold><name></bold></gold>"
    var guildInfoTag: String = "<green>Tag: </green><aqua><bold>[<tag>]</bold></aqua>"
    var guildInfoLeader: String = "<green>Leader: </green><gold><bold><leader></bold></gold>"
    var guildInfoFriendlyFire: String = "<green>Friendly Fire: </green><status>"
    var guildInfoMembers: String = "<green><bold>Members (<count>):</bold></green>"
    var guildMemberEntry: String = "  <gray>- </gray><gold><member></gold><roleColor> <role></roleColor>"
    var guildHelpHeader: String = "<aqua><bold>--- Guild Commands ---</bold></aqua>"
    var guildHelpCreate: String = "<green>/guild create <name> <tag></green>"
    var guildHelpInvite: String = "<green>/guild invite <player></green>"
    var guildHelpKick: String = "<green>/guild kick <player></green>"
    var guildHelpPromote: String = "<green>/guild promote <player></green>"
    var guildHelpLeave: String = "<green>/guild leave</green>"
    var guildHelpToggleFF: String = "<green>/guild toggleff</green>"
    var guildHelpInfo: String = "<green>/guild info</green>"
    var guildError: String = "<prefix><red><error></red>"

    // Direct message system
    var dmUsage: String = "<prefix><red>Usage: /msg <player> <message></red>"
    var dmCannotSelfMessage: String = "<prefix><red>You cannot send a message to yourself</red>"
    var dmCannotSendToPlayer: String = "<prefix><red>You cannot send a direct message to this player right now</red>"
    var dmReceived: String = "<light_purple><bold>[DM] </bold></light_purple><sender> <gray>-> </gray><aqua>You</aqua><white>: <message></white>"
    var dmSent: String = "<light_purple><bold>[DM] </bold></light_purple><aqua>You</aqua> <gray>-> </gray><receiver><white>: <message></white>"

    // Game setup messages
    var gameSetupInstruction: String = "<prefix><green>Use /gamesetup to open the setup GUI or use commands like /setlocation, /playeramount</green>"
    var gameSetupActive: String = "<prefix><red>You already have an active game setup. Save or delete it first.</red>"
    var locationRemoved: String = "<prefix><green>Spawn location removed</green>"
    var locationRemoveFailed: String = "<prefix><red>Failed to remove spawn location</red>"
    var hubSet: String = "<prefix><green>Hub location has been set</green>"
    var playerAmountUpdated: String = "<prefix><green>Player amount updated to <count></green>"

    // System messages
    var pluginEnabled: String = "<prefix><green>Skylife has been started!</green>"
    var pluginDisabled: String = "<prefix><red>Skylife has been stopped!</red>"
    var configLoaded: String = "<prefix><green>Configuration loaded successfully!</green>"
    var configSaved: String = "<prefix><green>Configuration saved!</green>"
    var messagesLoaded: String = "<prefix><green>Messages loaded!</green>"
    var messagesSaved: String = "<prefix><green>Messages saved!</green>"
    var messagesExported: String = "<prefix><green><bold>Messages exported to messages.json!</bold></green>"
    var messagesRestartNote: String = "<green>Restart the server to load any changes you make.</green>"

    // Legacy skill messages (for backwards compatibility)
    var skillError: String = "<prefix><red><error></red>"
    var skillPurchaseFailed: String = "<prefix><red>Failed to purchase skill: </red><gold><error></gold>"
    var skillMustUnlock: String = "<prefix><red>You must unlock this skill before you can select it!</red>"
    var skillSelectionFailedSlotsFull: String = "<prefix><red>You already have <max> skills selected. Unselect one first.</red>"

    // Admin command messages
    var noActiveSetup: String = "<prefix><red>No active setup! Use /game create <name> or /gamesetup <name> first.</red>"
    var gamePatternNotFound: String = "<prefix><red>Game pattern '<game>' not found!</red>"
    var amountMustBeAtLeastOne: String = "<prefix><red>Amount must be at least 1!</red>"
    var maxPlayersGreaterThanMin: String = "<prefix><red>Max players must be >= min players (<min>)!</red>"
    var onlyPlayersCanUse: String = "<prefix><red>This command can only be used by players!</red>"
    var configurationSaved: String = "<prefix><green>Configuration saved!</green>"
    var configurationLoaded: String = "<prefix><green>Configuration loaded!</green>"

    // Chat format (guild chat, party chat, game chat)
    var chatGlobal: String = "<sender><gray>: </gray><white><message></white>"
    var chatGuild: String = "<light_purple>[G] </light_purple><sender><gray>: </gray><white><message></white>"
    var chatParty: String = "<blue>[P] </blue><sender><gray>: </gray><white><message></white>"
    var chatGame: String = "<yellow>[Game] </yellow><sender><gray>: </gray><white><message></white>"
    var chatSpectator: String = "<gray>[Spectator] </gray><sender><gray>: </gray><white><message></white>"

    // Title messages
    var titleGameStarted: String = "<green>Game Started!</green>"
    var titleGameStartedSubtitle: String = "<gold>Survive and be the last one standing!</gold>"
    var titleVictory: String = "<gold>VICTORY!</gold>"
    var titleVictorySubtitle: String = "<green>You are the champion!</green>"
    var titleGameOver: String = "<red>Game Over!</red>"
    var titleGameOverSubtitle: String = "<green><winner> won the game!</green>"
    var titlePvpEnabled: String = "<red>PvP Enabled!</red>"
    var titlePvpEnabledSubtitle: String = "<green>Fight!</green>"
    var titleProtectionCountdown: String = "<green>Protection: <seconds></green>"

    // GUI text - Game Setup GUI
    var guiGameSetupDecreaseMinPlayers: String = "<red><bold>Decrease Min Players</bold></red>"
    var guiGameSetupDecreaseMinPlayersLore: String = GUI_CLICK_TO_DECREASE
    var guiGameSetupMinPlayers: String = "<yellow><bold>Min Players</bold></yellow>"
    var guiGameSetupMinPlayersLore1: String = "<white>Current: <count></white>"
    var guiGameSetupMinPlayersLore2: String = "<gray>(Minimum: 1)</gray>"
    var guiGameSetupIncreaseMinPlayers: String = "<green><bold>Increase Min Players</bold></green>"
    var guiGameSetupIncreaseMinPlayersLore: String = GUI_CLICK_TO_INCREASE
    var guiGameSetupDecreaseMaxPlayers: String = "<red><bold>Decrease Max Players</bold></red>"
    var guiGameSetupDecreaseMaxPlayersLore: String = GUI_CLICK_TO_DECREASE
    var guiGameSetupMaxPlayers: String = "<yellow><bold>Max Players</bold></yellow>"
    var guiGameSetupMaxPlayersLore1: String = "<white>Current: <count></white>"
    var guiGameSetupMaxPlayersLore2: String = "<gray>(Must be ≥ Min Players)</gray>"
    var guiGameSetupIncreaseMaxPlayers: String = "<green><bold>Increase Max Players</bold></green>"
    var guiGameSetupIncreaseMaxPlayersLore: String = GUI_CLICK_TO_INCREASE
    var guiGameSetupLobbyLocation: String = "<bold>Lobby Location</bold>"
    var guiGameSetupLobbyLocationSet: String = "<green>✓ Location set!</green>"
    var guiGameSetupLobbyLocationNotSet: String = GUI_CLICK_TO_SET_LOCATION
    var guiGameSetupSpectatorLocation: String = "<bold>Spectator Location</bold>"
    var guiGameSetupSpectatorLocationSet: String = "<green>✓ Location set!</green>"
    var guiGameSetupSpectatorLocationNotSet: String = GUI_CLICK_TO_SET_LOCATION
    var guiGameSetupMainLocation: String = "<bold>Main Location</bold>"
    var guiGameSetupMainLocationSet: String = "<green>✓ Location set!</green>"
    var guiGameSetupMainLocationNotSet: String = GUI_CLICK_TO_SET_LOCATION
    var guiGameSetupSpawnLocations: String = "<aqua><bold>Spawn Locations</bold></aqua>"
    var guiGameSetupSpawnLocationsLore1: String = "<white>Current spawns: <count></white>"
    var guiGameSetupSpawnLocationsLore2: String = "<gray>Click to add your current location</gray>"
    var guiGameSetupSpawnLocationsLore3: String = "<yellow>Use /removespawn <number> to remove</yellow>"
    var guiGameSetupCancel: String = "<red><bold>✗ Cancel Setup</bold></red>"
    var guiGameSetupCancelLore1: String = "<gray>Discard this setup without saving</gray>"
    var guiGameSetupCancelLore2: String = "<dark_red>This cannot be undone!</dark_red>"
    var guiGameSetupSave: String = "<green><bold>✓ Save Game Pattern</bold></green>"
    var guiGameSetupSaveLore: String = "<green>Click to save the game pattern!</green>"
    var guiGameSetupMissingRequirements: String = "<red>Missing requirements:</red>"
    var guiGameSetupMissingItem: String = "<gray>• <requirement></gray>"

    // GUI text - Skills GUI
    var guiSkillsTitle: String = "<light_purple><bold>Skills</bold></light_purple>"
    var guiSkillsLore1: String = "<gray>Click to select your skills</gray>"
    var guiSkillsLore2: String = "<yellow>You can choose up to </yellow><gold><bold>2 skills</bold></gold>"
    var guiSkillsInfo: String = "<aqua><bold>Skill Selection Info</bold></aqua>"
    var guiSkillsInfoSelected: String = "<gray>Selected: </gray><green><count>/2</green>"
    var guiSkillsInfoClickToSelect: String = "<yellow>Click skills to select/unselect them</yellow>"
    var guiSkillSelected: String = "<green><bold>✔ SELECTED</bold></green>"
    var guiSkillClickToSelect: String = "<yellow>Click to select</yellow>"

    // GUI text - Kit Selector
    var guiKitItems: String = "<gray>Items:</gray>"
    var guiKitItem: String = " <dark_gray>- </dark_gray><aqua><amount>x <item></aqua>"
    var guiKitClickToSelect: String = "<yellow><bold>Click to select!</bold></yellow>"

    // Skill item names
    var skillLuckyEggName: String = "<gold>Lucky Egg</gold>"
    var skillLuckyEggLore: String = "<gray>Throw for a random effect!</gray>"

    // Stats loading
    var statsLoaded: String = "<prefix><green>Stats loaded!</green>"

    // Game Overview GUI
    var guiGameOverviewTitle: String = "Spieleübersicht"
    var guiGameOverviewNextPage: String = "Nächste Seite"
    var guiGameOverviewPreviousPage: String = "Vorherige Seite"
    var guiGameOverviewOpenList: String = "Liste der Spiele öffnen"
    var guiGameOverviewPlayers: String = "Spieler: <current>/<max>"

    // Skills GUI Title
    var guiSkillSelectorTitle: String = "Select Your Skills"
    var guiSkillSelectionInfo: String = "Skill Selection Info"

    // Other GUI titles
    var guiSkillOverviewTitle: String = "Skill Overview"

    // Items/Maps
    var itemPaderbornName: String = "Paderborn"

    // Error messages - skills
    var skillFailedToSelect: String = "Failed to select skill"

    // Leaderboard messages
    var leaderboardHeader: String = "<prefix><gold>═════════════════════════════</gold>\n<prefix><yellow><bold>🏆 Top 10 - <stat></bold></yellow>\n<prefix><gold>═════════════════════════════</gold>"
    var leaderboardEmpty: String = "<prefix><gray>No players found in the leaderboard.</gray>"
    var leaderboardEntry: String = "<prefix><gray>#<rank></gray> <gold><player></gold> <gray>-</gray> <green><value></green>"
    var leaderboardFooter: String = "<prefix><gold>═════════════════════════════</gold>"
    var leaderboardInvalidStat: String = "<prefix><red>Invalid stat type! Use: points, kills, wins, games, kd</red>"

    // Priority join messages
    var priorityJoinKicked: String = "<prefix><gold><bold><kicker></bold></gold><green> with higher priority joined the game. You have been moved to the lobby.</green>"
    var priorityJoinFull: String = "<prefix><red>Game is full and no players with lower priority could be found!</red>"

    // Vanish messages
    var vanishEnabled: String = "<prefix><green><bold>Vanish enabled!</bold></green><gray> You are now invisible.</gray>"
    var vanishDisabled: String = "<prefix><green><bold>Vanish disabled!</bold></green><gray> You are now visible.</gray>"
    var vanishEnabledOther: String = "<prefix><green><bold>Vanish enabled for </bold></green><gold><bold><player></bold></gold>"
    var vanishDisabledOther: String = "<prefix><green><bold>Vanish disabled for </bold></green><gold><bold><player></bold></gold>"
    var vanishUsage: String = "<prefix><red>Usage: /vanish [player]</red>"
    var onlyPlayers: String = "<prefix><red>This command can only be used by players!</red>"
    var noPermission: String = "<prefix><red>You don't have permission to use this command!</red>"
}
