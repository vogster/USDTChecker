package com.dizonavr.database

import com.dizonavr.config.Config
import java.sql.DriverManager

class DatabaseManager {
    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {
        DriverManager.getConnection("jdbc:sqlite:${Config.DB_NAME}").use { connection ->
            connection.createStatement().executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS addresses (
                    address TEXT UNIQUE
                )
                """.trimIndent()
            )
        }
    }

    fun countMatches(addresses: List<String>): Int {
        return DriverManager.getConnection("jdbc:sqlite:${Config.DB_NAME}").use { connection ->
            addresses.chunked(Config.MAX_SQL_PARAMS).sumOf { chunk ->
                val placeholders = chunk.joinToString(",") { "?" }
                val query = "SELECT COUNT(*) FROM addresses WHERE address IN ($placeholders)"

                connection.prepareStatement(query).use { statement ->
                    chunk.forEachIndexed { index, address ->
                        statement.setString(index + 1, address)
                    }
                    statement.executeQuery().use { resultSet ->
                        if (resultSet.next()) resultSet.getInt(1) else 0
                    }
                }
            }
        }
    }

    fun getTotalAddressCount(): Int {
        return DriverManager.getConnection("jdbc:sqlite:${Config.DB_NAME}").use { connection ->
            connection.createStatement().executeQuery("SELECT COUNT(*) FROM addresses").use { resultSet ->
                if (resultSet.next()) resultSet.getInt(1) else 0
            }
        }
    }

    fun findMatches(addresses: List<String>): List<String> {
        return DriverManager.getConnection("jdbc:sqlite:${Config.DB_NAME}").use { conn ->
            addresses.chunked(Config.MAX_SQL_PARAMS).flatMap { chunk ->
                val placeholders = chunk.joinToString(",") { "?" }
                val sql = "SELECT address FROM addresses WHERE address IN ($placeholders)"

                conn.prepareStatement(sql).use { stmt ->
                    chunk.forEachIndexed { index, address ->
                        stmt.setString(index + 1, address)
                    }
                    stmt.executeQuery().use { rs ->
                        mutableListOf<String>().apply {
                            while (rs.next()) {
                                add(rs.getString("address"))
                            }
                        }
                    }
                }
            }
        }
    }
}