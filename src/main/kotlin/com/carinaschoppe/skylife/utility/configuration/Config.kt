package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("scoreboard_title")
    var scoreboardTitle: String = "<bold><aqua>Skylife</aqua></bold>",

    @SerializedName("scoreboard")
    var scoreboard: ScoreboardConfig = ScoreboardConfig(),

    @SerializedName("timer_settings")
    var timer: Timer = Timer(),

    @SerializedName("kits_enabled")
    var kitsEnabled: Boolean = true,

    @SerializedName("database")
    var database: DatabaseConfig = DatabaseConfig()
)

data class DatabaseConfig(
    @SerializedName("type")
    var type: String = "sqlite",

    @SerializedName("postgresql")
    var postgresql: PostgreSQLConfig = PostgreSQLConfig()
)

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
