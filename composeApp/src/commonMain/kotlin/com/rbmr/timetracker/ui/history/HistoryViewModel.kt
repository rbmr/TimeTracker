package com.rbmr.timetracker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.utils.ShareHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Instant

class HistoryViewModel(
    private val repository: WorkSessionRepository,
    private val shareHelper: ShareHelper
) : ViewModel() {

    val historicalSessions = repository.historicalSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun exportHistory(sessions: List<WorkSession>) {
        val csvHeader = "Start,End,Note\n"
        val csvBody = sessions.joinToString("\n") { session ->
            val startStr = Instant.fromEpochMilliseconds(session.startTime).toString()
            val endStr = session.endTime?.let { Instant.fromEpochMilliseconds(it).toString() } ?: "Ongoing"
            "$startStr,$endStr,${session.note}"
        }
        shareHelper.shareCsv(csvHeader + csvBody)
    }
}