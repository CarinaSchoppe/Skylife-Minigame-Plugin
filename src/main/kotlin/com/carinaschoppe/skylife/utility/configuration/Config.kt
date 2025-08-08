package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("scoreboard_title")
    var scoreboardTitle: String = "&l&bS&3k&by&3l&bi&3f&be",

    @SerializedName("timer_settings")
    var timer: Timer = Timer()
)
