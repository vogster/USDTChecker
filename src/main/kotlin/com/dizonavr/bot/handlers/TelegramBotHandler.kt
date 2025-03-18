package com.dizonavr.bot.handlers

import com.dizonavr.config.Config
import com.dizonavr.config.Messages
import com.dizonavr.database.DatabaseManager
import com.dizonavr.service.FileProcessor
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.logging.LogLevel
import java.io.File

class TelegramBotHandler(private val database: DatabaseManager) {

    fun start() {
        val bot = bot {
            token = Config.BOT_TOKEN
            logLevel = LogLevel.Error

            setupCommandHandlers()
        }
        bot.startPolling()
    }

    private fun Bot.Builder.setupCommandHandlers() {
        dispatch {
            handleStartCommand()
            handleTextMessages()
            handleDocumentUploads()
        }
    }

    private fun Dispatcher.handleStartCommand() {
        command("start") {
            this.bot.sendFormattedMessage(
                chatId = message.chat.id,
                text = Messages.get("greeting")
            )
        }
    }

    private fun Dispatcher.handleTextMessages() {
        message {
            message.text?.let { text ->
                if (!text.contains("/")) {
                    val addresses = text.lines()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    this.bot.sendAnalysisResults(addresses, message.chat.id)
                }
            }
        }
    }

    private fun Dispatcher.handleDocumentUploads() {
        document {
            val document = message.document ?: return@document

            when {
                !isValidFileType(document) -> this.bot.sendInvalidFileTypeMessage(message.chat.id)
                else -> this.bot.processValidDocument(document, message.chat.id)
            }
        }
    }

    private fun Bot.processValidDocument(document: Document, chatId: Long) {
        val fileBytes = downloadFileBytes(document.fileId) ?: byteArrayOf()
        val tempFile = createTempFile(document)
        try {
            tempFile.writeBytes(fileBytes)
            val addresses = when {
                document.fileName.orEmpty().endsWith(".txt") ->
                    FileProcessor().processTextFile(tempFile.path)

                document.fileName.orEmpty().endsWith(".csv") ->
                    FileProcessor().processCsvFile(tempFile.path)

                else -> {
                    sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = Messages.get("error.invalid_file_type")
                    )
                    emptyList()
                }
            }

            handleAddressList(addresses, document.fileName ?: "", chatId)
        } catch (e: Exception) {
            sendErrorMessage(e, chatId)
        } finally {
            tempFile.deleteIfExists()
        }
    }

    private fun isValidFileType(document: Document): Boolean {
        return document.fileName.orEmpty().let { fileName ->
            fileName.endsWith(".txt") || fileName.endsWith(".csv")
        }
    }

    private fun createTempFile(document: Document): File {
        return File(document.fileId).apply {
            if (exists()) delete()
        }
    }

    private fun File.deleteIfExists() {
        takeIf { exists() }?.delete()
    }

    private fun Bot.handleAddressList(addresses: List<String>, fileName: String, chatId: Long) {
        when {
            addresses.isEmpty() -> sendEmptyFileMessage(chatId, fileName)
            else -> sendAnalysisResults(addresses, chatId)
        }
    }

    private fun Bot.sendAnalysisResults(addresses: List<String>, chatId: Long) {
        val matches = database.findMatches(addresses).distinct()
        val totalInDatabase = database.getTotalAddressCount()
        val matchesCount = matches.size

        val matchesText = when {
            matches.isEmpty() -> Messages.get("no_matches")
            matches.size <= 10 -> Messages.get("matches_list", matches.joinToString("\n• "))
            else -> {
                sendMatchesFile(matches, chatId)
                Messages.get("file_matches")
            }
        }

        val fullMessage = Messages.get(
            "results",
            addresses.size,
            matchesCount,
            totalInDatabase,
            matchesText
        )

        sendFormattedMessage(chatId, fullMessage)
    }

    private fun Bot.sendMatchesFile(matches: List<String>, chatId: Long) {
        val tempFile = createTempFile("matches_${chatId}_", ".txt").apply {
            writeText(matches.joinToString("\n"))
        }

        sendDocument(
            chatId = ChatId.fromId(chatId),
            document = TelegramFile.ByFile(tempFile)
        )

        tempFile.deleteOnExit()
    }

    private fun Bot.sendFormattedMessage(chatId: Long, text: String) {
        sendMessage(
            chatId = ChatId.fromId(chatId),
            text = text.trimIndent(),
            parseMode = ParseMode.MARKDOWN
        )
    }

    private fun Bot.sendInvalidFileTypeMessage(chatId: Long) {
        sendMessage(
            chatId = ChatId.fromId(chatId),
            text = Messages.get("error.invalid_file_type")
        )
    }

    private fun Bot.sendEmptyFileMessage(chatId: Long, fileName: String) {
        sendMessage(
            chatId = ChatId.fromId(chatId),
            text = Messages.get("error.empty_file", fileName)
        )
    }

    private fun Bot.sendErrorMessage(e: Exception, chatId: Long) {
        sendMessage(
            chatId = ChatId.fromId(chatId),
            text = Messages.get("error.processing", e.message ?: "Неизвестная ошибка")
        )
    }
}