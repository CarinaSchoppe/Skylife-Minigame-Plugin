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
    var skillPrices: SkillPriceConfig = SkillPriceConfig(),

    @SerializedName("max_skills")
    var maxSkills: MaxSkillsConfig = MaxSkillsConfig(),

    @SerializedName("priority_join")
    var priorityJoin: PriorityJoinConfig = PriorityJoinConfig()
)








