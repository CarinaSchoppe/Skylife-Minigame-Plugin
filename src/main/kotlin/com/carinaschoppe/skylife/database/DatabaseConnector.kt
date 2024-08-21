package com.carinaschoppe.skylife.database

import org.jetbrains.exposed.sql.Database
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
    }

}