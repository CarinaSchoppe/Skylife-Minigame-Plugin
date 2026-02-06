package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class DatabaseConfig(
    @SerializedName("type")
    var type: String = "sqlite",

    @SerializedName("postgresql")
    var postgresql: PostgreSQLConfig = PostgreSQLConfig()
)