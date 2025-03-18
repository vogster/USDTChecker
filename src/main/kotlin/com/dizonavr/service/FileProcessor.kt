package com.dizonavr.service

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class FileProcessor {
    private val csvMapper = CsvMapper()

    fun processTextFile(filePath: String): List<String> {
        return File(filePath).readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun processCsvFile(filePath: String): List<String> {
        val schema = CsvSchema.emptySchema().withHeader()
        return csvMapper
            .readerFor(Map::class.java)
            .with(schema)
            .readValues<Map<String, String>>(File(filePath))
            .readAll()
            .mapNotNull { it[COLUMN_FROM] }
    }

    companion object {

        const val COLUMN_FROM = "From"
    }
}