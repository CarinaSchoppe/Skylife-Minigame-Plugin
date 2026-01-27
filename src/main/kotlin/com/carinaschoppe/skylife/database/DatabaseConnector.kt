package com.carinaschoppe.skylife.database

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.database.DatabaseConnector.connectDatabase
import com.carinaschoppe.skylife.guild.GuildMembers
import com.carinaschoppe.skylife.guild.Guilds
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsPlayers
import org.bukkit.Bukkit
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

/**
 * Handles the connection and initialization of the SQLite database for the Skylife plugin.
 * This singleton object manages the database connection and ensures proper schema setup.
 */
object DatabaseConnector {

    /**
     * The active database connection.
     * This is initialized when [connectDatabase] is called.
     */
    lateinit var database: Database

    /**
     * Establishes a connection to the SQLite database and initializes the required tables.
     * If the database file doesn't exist, it will be created automatically.
     *
     * The database is stored at `plugins/Skylife/database.db`.
     * After successful connection, it creates all necessary tables defined in the database schema.
     *
     * @throws IllegalStateException if there's an error creating the database file or connecting to it.
     */
    fun connectDatabase() {
        val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "database.db")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        val url = "jdbc:sqlite:${file.absolutePath}"
        database = Database.connect(url)
        Bukkit.getServer().consoleSender.sendMessage(Messages.DATABASE_CONNECTED)
        transaction {
            SchemaUtils.create(StatsPlayers, Guilds, GuildMembers)
        }
        Bukkit.getServer().consoleSender.sendMessage(Messages.DATABASE_TABLES_CREATED)

    }

}
