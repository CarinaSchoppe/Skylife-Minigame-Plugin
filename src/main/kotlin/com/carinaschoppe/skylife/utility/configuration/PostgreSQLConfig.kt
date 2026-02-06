package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName


data class PostgreSQLConfig(
    @SerializedName("host")
    var host: String = "localhost",

    @SerializedName("port")
    var port: Int = 5432,

    @SerializedName("database")
    var database: String = "skylife",

    @SerializedName("username")
    var username: String = "postgres",

    @SerializedName("password")
    var password: String = "password"
)