package com.rbmr.timetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.utils.ToastManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val repository: WorkSessionRepository,
    private val toastManager: ToastManager // Add parameter
) : ViewModel() {

    val ongoingSession = repository.ongoingSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onPunchIn() {
        if (ongoingSession.value == null) {
            viewModelScope.launch {
                try {
                    val newSession = WorkSession(
                        startTime = Clock.System.now().toEpochMilliseconds(),
                        endTime = null,
                        note = ""
                    )
                    repository.insert(newSession)
                } catch (e: Exception) {
                    toastManager.show("Error starting session") // Error Toast
                }
            }
        }
    }

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
                try {
                    repository.delete(session)
                    toastManager.show("Session deleted") // Toast
                } catch (e: Exception) {
                    toastManager.show("Error deleting session") // Error Toast
                }
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