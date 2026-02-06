package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class PriorityJoinConfig(
    @SerializedName("enabled_for_vip")
    var enabledForVip: Boolean = true,

    @SerializedName("enabled_for_vip_plus")
    var enabledForVipPlus: Boolean = true,

    @SerializedName("enabled_for_staff")
    var enabledForStaff: Boolean = true
)