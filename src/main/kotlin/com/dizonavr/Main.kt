package com.dizonavr

import com.dizonavr.bot.handlers.TelegramBotHandler
import com.dizonavr.config.Messages
import com.dizonavr.database.DatabaseManager

fun main() {
    val databaseManager = DatabaseManager()
    TelegramBotHandler(databaseManager).start()
}