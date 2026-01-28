package com.carinaschoppe.skylife.database

import com.carinaschoppe.skylife.Skylife
import com.carinaschoppe.skylife.database.DatabaseConnector.connectDatabase
import com.carinaschoppe.skylife.economy.KitUnlockTable
import com.carinaschoppe.skylife.economy.PlayerEconomyTable
import com.carinaschoppe.skylife.guild.GuildMembers
import com.carinaschoppe.skylife.guild.Guilds
import com.carinaschoppe.skylife.skills.PlayerSkills
import com.carinaschoppe.skylife.skills.SkillUnlockTable
import com.carinaschoppe.skylife.utility.messages.Messages
import com.carinaschoppe.skylife.utility.statistics.StatsPlayers
import org.bukkit.Bukkit
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

/**
 * Handles the connection and initialization of the database for the Skylife plugin.
 * Supports both SQLite and PostgreSQL databases based on configuration.
 * This singleton object manages the database connection and ensures proper schema setup.
 */
object DatabaseConnector {

    /**
     * The active database connection.
     * This is initialized when [connectDatabase] is called.
     */
    lateinit var database: Database

    /**
     * Establishes a connection to the database (SQLite or PostgreSQL) and initializes the required tables.
     * The database type is determined by the configuration file.
     *
     * For SQLite:
     * - Database is stored at `plugins/Skylife/database.db`
     * - File is created automatically if it doesn't exist
     *
     * For PostgreSQL:
     * - Connects to the remote database specified in config.json
     * - Database must already exist on the PostgreSQL server
     *
     * After successful connection, it creates all necessary tables defined in the database schema.
     *
     * @throws IllegalStateException if there's an error creating the database file or connecting to it.
     */
    fun connectDatabase() {
        try {
            val config = Skylife.config
            val dbType = config.database.type.lowercase().trim()

            // Validate database type
            if (dbType != "sqlite" && dbType != "postgresql") {
                Bukkit.getServer().consoleSender.sendMessage(
                    Messages.PREFIX.append(
                        net.kyori.adventure.text.Component.text(
                            "Invalid database type '$dbType' in config! Using SQLite as fallback.",
                            net.kyori.adventure.text.format.NamedTextColor.YELLOW
                        )
                    )
                )
            }

            val url = when (dbType) {
                "postgresql" -> {
                    val pgConfig = config.database.postgresql

                    // Validate PostgreSQL config
                    if (pgConfig.host.isBlank() || pgConfig.database.isBlank()) {
                        throw IllegalArgumentException("PostgreSQL configuration incomplete! Host and database name are required.")
                    }

                    "jdbc:postgresql://${pgConfig.host}:${pgConfig.port}/${pgConfig.database}"
                }

                else -> {
                    // Default to SQLite
                    val file = File(Bukkit.getServer().pluginsFolder, Skylife.folderLocation + "database.db")
                    if (!file.exists()) {
                        file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    "jdbc:sqlite:${file.absolutePath}"
                }
            }

            database = if (dbType == "postgresql") {
                val pgConfig = config.database.postgresql
                Database.connect(
                    url = url,
                    driver = "org.postgresql.Driver",
                    user = pgConfig.username,
                    password = pgConfig.password
                )
            } else {
                Database.connect(url)
            }

            Bukkit.getServer().consoleSender.sendMessage(Messages.DATABASE_CONNECTED)

            transaction {
                SchemaUtils.create(StatsPlayers, Guilds, GuildMembers, PlayerSkills, PlayerEconomyTable, KitUnlockTable, SkillUnlockTable)
            }

            Bukkit.getServer().consoleSender.sendMessage(Messages.DATABASE_TABLES_CREATED)

        } catch (e: Exception) {
            Bukkit.getServer().consoleSender.sendMessage(
                Messages.PREFIX.append(
                    net.kyori.adventure.text.Component.text(
                        "Failed to connect to database: ${e.message}",
                        net.kyori.adventure.text.format.NamedTextColor.RED
                    )
                )
            )
            e.printStackTrace()
            throw IllegalStateException("Database connection failed", e)
        }
    }

}
