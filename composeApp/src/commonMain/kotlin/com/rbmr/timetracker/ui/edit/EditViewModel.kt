package com.rbmr.timetracker.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.WorkSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditViewModel(
    private val repository: WorkSessionRepository,
    private val sessionId: Long
) : ViewModel() {

    private val _sessionState = MutableStateFlow<WorkSession?>(null)
    val sessionState: StateFlow<WorkSession?> = _sessionState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSessionById(sessionId).collect {
                _sessionState.value = it
            }
        }
    }

    fun updateSession(updated: WorkSession) {
        viewModelScope.launch {
            repository.update(updated)
        }
    }

    fun saveAndExit(
        currentDraft: WorkSession,
        endTimeInMillis: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            // Apply the End Time (Punch Out logic)
            val finalSession = currentDraft.copy(endTime = endTimeInMillis)
            repository.update(finalSession)
            onSuccess()
        }
    }

    fun deleteSession(onSuccess: () -> Unit) {
        _sessionState.value?.let {
            viewModelScope.launch {
                repository.delete(it)
                onSuccess()
            }
        }
    }
}