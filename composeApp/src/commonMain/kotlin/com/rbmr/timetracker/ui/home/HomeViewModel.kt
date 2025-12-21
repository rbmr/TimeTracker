package com.rbmr.timetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.WorkSession
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock

@OptIn(FlowPreview::class)
class HomeViewModel(private val repository: WorkSessionRepository) : ViewModel() {

    // One stream to rule them all: Null = Idle, Session = Working
    val ongoingSession = repository.ongoingSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // IDLE LOGIC
    fun onPunchIn() {
        if (ongoingSession.value == null) {
            viewModelScope.launch {
                val newSession = WorkSession(
                    startTime = Clock.System.now().toEpochMilliseconds(),
                    endTime = null,
                    note = ""
                )
                repository.insert(newSession)
            }
        }
    }

    // WORKING LOGIC (Merged from WorkingViewModel)
    private val _noteUpdates = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            _noteUpdates
                .filterNotNull()
                .debounce(500)
                .collect { noteContent ->
                    ongoingSession.value?.let { current ->
                        repository.update(current.copy(note = noteContent))
                    }
                }
        }
    }

    fun onDeleteSession() {
        viewModelScope.launch {
            ongoingSession.value?.let { session ->
                repository.delete(session)
            }
        }
    }

    fun onNoteChange(newNote: String) {
        _noteUpdates.value = newNote
    }

    fun onUpdateStartTime(newStart: Long) {
        ongoingSession.value?.let { current ->
            viewModelScope.launch {
                repository.update(current.copy(startTime = newStart))
            }
        }
    }
}