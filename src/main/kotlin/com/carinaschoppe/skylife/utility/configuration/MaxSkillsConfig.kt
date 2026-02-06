package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class MaxSkillsConfig(
    @SerializedName("default")
    var default: Int = 2,

    @SerializedName("vip")
    var vip: Int = 3,

    @SerializedName("vip_plus")
    var vipPlus: Int = 4
)