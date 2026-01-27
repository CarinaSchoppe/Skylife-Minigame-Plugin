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
        "<dark_gray><strikethrough>----------------</strikethrough></dark_gray>",
        "<aqua>Server</aqua><gray>: </gray><white>{server}</white>",
        "<aqua>Map</aqua><gray>: </gray><white>{map}</white>",
        "<aqua>Alive</aqua><gray>: </gray><green>{alive}</green><gray>/</gray><green>{max}</green>",
        "<aqua>Kills</aqua><gray>: </gray><red>{kills}</red>",
        "<aqua>Kit</aqua><gray>: </gray><yellow>{kit}</yellow>",
        "<aqua>Rank</aqua><gray>: </gray><gold>#{rank}</gold>",
        "<dark_gray><strikethrough>----------------</strikethrough></dark_gray>"
    )
)
