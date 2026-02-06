package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class SkillPriceConfig(
    @SerializedName("common")
    var common: Int = 150,

    @SerializedName("rare")
    var rare: Int = 500,

    @SerializedName("epic")
    var epic: Int = 1500,

    @SerializedName("legendary")
    var legendary: Int = 5000
)