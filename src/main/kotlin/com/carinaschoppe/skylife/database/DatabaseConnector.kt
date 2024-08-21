package com.carinaschoppe.skylife.database

import com.carinaschoppe.skylife.utility.statistics.StatsPlayers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseConnector {


    lateinit var database: Database


    fun connectDatabase() {
        val file = File("/Skylife/database.db")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        val url = "jdbc:sqlite:${file.absolutePath}"
        database = Database.connect(url)

        transaction {
            SchemaUtils.create(StatsPlayers)
        }
    }

}