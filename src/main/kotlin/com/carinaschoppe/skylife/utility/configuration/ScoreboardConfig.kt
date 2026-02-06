package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class ScoreboardConfig(
    @SerializedName("server_name")
    var serverName: String = "Skylife",

    @SerializedName("title")
    var title: String = "<bold><aqua>Skylife</aqua></bold>",

    @SerializedName("animate_title")
    var animateTitle: Boolean = true,

    @SerializedName("lines")
    var lines: List<String> = listOf(
        DIVIDER_LINE,
        LINE_SERVER,
        "<aqua>Map</aqua><gray>: </gray><white>{map}</white>",
        "<aqua>Alive</aqua><gray>: </gray><green>{alive}</green><gray>/</gray><green>{max}</green>",
        LINE_KILLS,
        LINE_GUILD,
        "<aqua>Rank</aqua><gray>: </gray><gold>#{rank}</gold>",
        DIVIDER_LINE
    ),

    @SerializedName("lobby_title")
    var lobbyTitle: String = "<bold><aqua>Skylife Lobby</aqua></bold>",

    @SerializedName("lobby_lines")
    var lobbyLines: List<String> = listOf(
        DIVIDER_LINE,
        LINE_SERVER,
        "",
        "<aqua>Rank</aqua><gray>: </gray>{rank_color}{rank}",
        LINE_GUILD,
        "<aqua>Stats Rank</aqua><gray>: </gray><gold>#{player_rank}</gold>",
        "<aqua>Coins</aqua><gray>: </gray><gold>{coins}</gold>",
        "",
        LINE_KILLS,
        "<aqua>Wins</aqua><gray>: </gray><yellow>{wins}</yellow>",
        "<aqua>Games</aqua><gray>: </gray><white>{games}</white>",
        DIVIDER_LINE
    )
) {
    private companion object {
        const val DIVIDER_LINE = "<dark_gray><strikethrough>----------------</strikethrough></dark_gray>"
        const val LINE_SERVER = "<aqua>Server</aqua><gray>: </gray><white>{server}</white>"
        const val LINE_GUILD = "<aqua>Guild</aqua><gray>: </gray><light_purple>{guild}</light_purple>"
        const val LINE_KILLS = "<aqua>Kills</aqua><gray>: </gray><red>{kills}</red>"
    }
}
