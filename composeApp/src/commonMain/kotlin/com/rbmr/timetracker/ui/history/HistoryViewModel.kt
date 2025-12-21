package com.rbmr.timetracker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.utils.CsvUtils
import com.rbmr.timetracker.utils.ShareHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val repository: WorkSessionRepository,
    private val shareHelper: ShareHelper
) : ViewModel() {

    val historicalSessions = repository.historicalSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun exportHistory(sessions: List<WorkSession>) {
        val csvContent = CsvUtils.sessionsToCsv(sessions)
        shareHelper.shareCsv(csvContent)
    }
}