package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("scoreboard_title")
    var scoreboardTitle: String = "<bold><aqua>Skylife</aqua></bold>",

    @SerializedName("scoreboard")
    var scoreboard: ScoreboardConfig = ScoreboardConfig(),

    @SerializedName("timer_settings")
    var timer: Timer = Timer()
)
