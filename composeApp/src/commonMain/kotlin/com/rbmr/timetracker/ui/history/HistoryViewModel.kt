package com.rbmr.timetracker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    repository: WorkSessionRepository
) : ViewModel() {

    val historicalSessions = repository.historicalSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}