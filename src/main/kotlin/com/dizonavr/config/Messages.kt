package com.dizonavr.config

import java.io.InputStreamReader
import java.io.Reader
import java.util.*

object Messages {
    private val properties = Properties()

    init {
        val inputStream = this::class.java.classLoader.getResourceAsStream("messages.properties")
        val reader: InputStreamReader? = inputStream?.let { InputStreamReader(it, "UTF-8") }
        properties.load(reader)
    }

    fun get(key: String, vararg args: Any): String = properties.getProperty(key).format(*args)
}