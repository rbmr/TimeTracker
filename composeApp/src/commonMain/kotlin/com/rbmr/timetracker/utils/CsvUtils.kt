package com.rbmr.timetracker.utils

import com.rbmr.timetracker.data.database.WorkSession
import kotlin.time.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv

object CsvUtils {

    /**
     * Internal class used ONLY for CSV generation.
     * This separates your Database "shape" (Long timestamps) from your CSV "shape" (ISO Strings).
     */
    @Serializable
    private data class CsvSession(
        val id: Long,
        val startTime: String, // ISO-8601
        val endTime: String?,  // ISO-8601 or null/empty
        val note: String       // Library handles newlines/quotes automatically
    )

    /**
     * Converts Database sessions -> CSV String
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun sessionsToCsv(sessions: List<WorkSession>): String {
        // Use comma by default for export
        val csv = Csv {
            hasHeaderRecord = true
            ignoreUnknownColumns = true
            delimiter = ','
        }

        // Convert Database objects to CSV-friendly objects
        val exportList = sessions.map { session ->
            CsvSession(
                id = session.id,
                startTime = Instant.fromEpochMilliseconds(session.startTime).toString(),
                endTime = session.endTime?.let { Instant.fromEpochMilliseconds(it).toString() },
                note = session.note
            )
        }

        // Let the library handle the serialization
        return csv.encodeToString(ListSerializer(CsvSession.serializer()), exportList)
    }

    /**
     * Converts CSV String -> Database sessions
     * Throws exception if format is invalid.
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun csvToSessions(csvContent: String): List<WorkSession> {
        if (csvContent.isBlank()) return emptyList()

        val firstLine = csvContent.lineSequence().firstOrNull() ?: return emptyList()
        val commaCount = firstLine.count { it == ',' }
        val tabCount = firstLine.count { it == '\t' }
        val separator = if (tabCount > commaCount) '\t' else ','
        val csv = Csv {
            hasHeaderRecord = true
            ignoreUnknownColumns = true
            delimiter = separator
        }

        // 1. Let the library parse the CSV string
        val importList = csv.decodeFromString(ListSerializer(CsvSession.serializer()), csvContent)

        // 2. Convert CSV objects back to Database objects
        return importList.map { csvSession ->
            WorkSession(
                id = csvSession.id,
                startTime = Instant.parse(csvSession.startTime).toEpochMilliseconds(),
                endTime = csvSession.endTime?.takeIf { it.isNotBlank() }?.let {
                    Instant.parse(it).toEpochMilliseconds()
                },
                note = csvSession.note
            )
        }
    }
}