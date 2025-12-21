package com.rbmr.timetracker.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.utils.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditViewModel(
    private val repository: WorkSessionRepository,
    private val toastManager: ToastManager,
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
            // No toast here as this happens continuously while editing
        }
    }

    fun saveAndExit(
        currentDraft: WorkSession,
        endTimeInMillis: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val finalSession = currentDraft.copy(endTime = endTimeInMillis)
                repository.update(finalSession)
                toastManager.show("Session saved")
                onSuccess()
            } catch (e: Exception) {
                toastManager.show("Error saving session")
            }
        }
    }

    fun deleteSession(onSuccess: () -> Unit) {
        _sessionState.value?.let {
            viewModelScope.launch {
                try {
                    repository.delete(it)
                    toastManager.show("Session deleted")
                    onSuccess()
                } catch (e: Exception) {
                    toastManager.show("Error deleting session")
                }
            }
        }
    }
}