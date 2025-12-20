package com.rbmr.timetracker.ui.working

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.WorkSession
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class WorkingViewModel(private val repository: WorkSessionRepository) : ViewModel() {

    val session = repository.ongoingSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // We keep a local state for the note to allow smooth typing without DB spam
    private val _noteUpdates = MutableStateFlow<String?>(null)

    init {
        // Auto-save note logic: Debounce writes by 500ms
        viewModelScope.launch {
            _noteUpdates
                .filterNotNull()
                .debounce(500)
                .collect { noteContent ->
                    session.value?.let { current ->
                        repository.update(current.copy(note = noteContent))
                    }
                }
        }
    }

    fun onNoteChange(newNote: String) {
        _noteUpdates.value = newNote
    }

    fun onUpdateStartTime(newStart: Long) {
        session.value?.let { current ->
            viewModelScope.launch {
                repository.update(current.copy(startTime = newStart))
            }
        }
    }

    fun onDeleteSession(onDeleted: () -> Unit) {
        session.value?.let { current ->
            viewModelScope.launch {
                repository.delete(current)
                onDeleted()
            }
        }
    }
}