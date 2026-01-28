package com.carinaschoppe.skylife.utility.configuration

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("scoreboard_title")
    var scoreboardTitle: String = "<bold><aqua>Skylife</aqua></bold>",

    @SerializedName("scoreboard")
    var scoreboard: ScoreboardConfig = ScoreboardConfig(),

    @SerializedName("timer_settings")
    var timer: Timer = Timer(),

    @SerializedName("database")
    var database: DatabaseConfig = DatabaseConfig(),

    @SerializedName("skill_prices")
    var skillPrices: SkillPriceConfig = SkillPriceConfig()
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

data class SkillPriceConfig(
    @SerializedName("common")
    var common: Int = 0,

    @SerializedName("rare")
    var rare: Int = 500,

    @SerializedName("epic")
    var epic: Int = 1500,

    @SerializedName("legendary")
    var legendary: Int = 5000
)
