package com.rbmr.timetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.WorkSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock

class HomeViewModel(private val repository: WorkSessionRepository) : ViewModel() {

    // Expose a "Hot" Flow that is null-safe for the UI
    val ongoingSession: StateFlow<WorkSession?> = repository.ongoingSession
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onPunchIn(onNavigateToWorking: () -> Unit) {
        // If we already have a session, just go to it.
        // Otherwise, create one.
        if (ongoingSession.value != null) {
            onNavigateToWorking()
        } else {
            viewModelScope.launch {
                val newSession = WorkSession(
                    startTime = Clock.System.now().toEpochMilliseconds(),
                    endTime = null,
                    note = ""
                )
                repository.insert(newSession)
                onNavigateToWorking()
            }
        }
    }
}