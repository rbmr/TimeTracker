package com.rbmr.timetracker.utils

import com.rbmr.timetracker.data.database.WorkSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CsvUtilsTest {

    // Mirror of the private class in CsvUtils, used to generate test inputs programmatically
    @Serializable
    data class TestCsvSession(
        val id: Long? = null,
        val startTime: String = "",
        val endTime: String? = null,
        val note: String = ""
    )

    private val session1 = WorkSession(
        id = 1L,
        startTime = Instant.parse("2024-01-01T09:00:00Z").toEpochMilliseconds(),
        endTime = Instant.parse("2024-01-01T17:00:00Z").toEpochMilliseconds(),
        note = ""
    )

    private val session2 = WorkSession(
        id = 2L,
        startTime = Instant.parse("2024-01-02T09:30:00Z").toEpochMilliseconds(),
        endTime = Instant.parse("2024-01-02T18:00:00Z").toEpochMilliseconds(),
        note = "Hello World"
    )

    private val session3 = WorkSession(
        id = 3L,
        startTime = Instant.parse("2024-01-03T08:00:00Z").toEpochMilliseconds(),
        endTime = null,
        // Difficult characters: tabs, newlines, commas, semicolons, quotes
        note = "Complex\tNote\nWith, Comma; Semicolon; and \"Quotes\""
    )

    private val testSessions = listOf(session1, session2, session3)

    @Test
    fun testStandardCsvRoundTrip() {
        val csvString = CsvUtils.sessionsToCsv(testSessions)
        println("Standard CSV:\n$csvString")

        val parsedSessions = CsvUtils.csvToSessions(csvString)
        assertEquals(testSessions, parsedSessions)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun testTsvImport() {
        // 1. Generate TSV (Tab Separated) using the CSV library
        val tsvFormat = Csv {
            hasHeaderRecord = true
            delimiter = '\t'
        }

        val rawData = testSessions.map {
            TestCsvSession(
                id = it.id,
                startTime = Instant.fromEpochMilliseconds(it.startTime).toString(),
                endTime = it.endTime?.let { t -> Instant.fromEpochMilliseconds(t).toString() },
                note = it.note
            )
        }

        val tsvString = tsvFormat.encodeToString(ListSerializer(TestCsvSession.serializer()), rawData)

        println("Generated TSV:\n$tsvString")
        assertTrue(tsvString.contains("\t"), "Generated string should contain tabs")

        // 2. Feed TSV string to CsvUtils (which should auto-detect the tab)
        val parsedSessions = CsvUtils.csvToSessions(tsvString)

        // 3. Verify
        assertEquals(testSessions, parsedSessions, "Should correctly parse TSV file")
    }
}