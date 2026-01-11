package com.rbmr.timetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.ui.form.SessionForm
import com.rbmr.timetracker.utils.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class HomeViewModel(
    private val repository: WorkSessionRepository,
    private val toastManager: ToastManager
) : ViewModel() {

    private val _sessionForm = MutableStateFlow<SessionForm?>(null)
    val sessionForm = _sessionForm.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ongoingSession.collect { dbSession ->
                if (dbSession == null) {
                    _sessionForm.value = null
                } else {
                    if (_sessionForm.value?.uiSession?.value?.id != dbSession.id) {
                        _sessionForm.value = SessionForm(
                            scope = viewModelScope,
                            repository = repository,
                            toastManager = toastManager,
                            initialSession = dbSession,
                            dbSessionFlow = repository.ongoingSession
                        )
                    }
                }
            }
        }
    }

    fun onPunchIn() {
        if (_sessionForm.value == null) {
            viewModelScope.launch {
                try {
                    val newSession = WorkSession(
                        startTime = Clock.System.now().toEpochMilliseconds(),
                        endTime = null,
                        note = ""
                    )
                    repository.insert(newSession)
                } catch (e: Exception) {
                    toastManager.show("Error starting session")
                }
            }
        }
    }

    fun onDeleteSession() {
        _sessionForm.value?.delete()
    }
}