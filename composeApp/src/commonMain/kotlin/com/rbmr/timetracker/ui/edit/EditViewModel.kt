package com.rbmr.timetracker.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.ui.form.SessionForm
import com.rbmr.timetracker.utils.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock

class EditViewModel(
    private val repository: WorkSessionRepository,
    private val toastManager: ToastManager,
    private val sessionId: Long
) : ViewModel() {

    private val _form = MutableStateFlow<SessionForm?>(null)
    val form: StateFlow<SessionForm?> = _form.asStateFlow()

    init {
        viewModelScope.launch {
            var initialSession = repository.getSessionById(sessionId).filterNotNull().first()

            // If the session is ongoing (endTime is null), we assume the user intends to stop it.
            // We fill endTime with the current time. This only affects the UI state in SessionForm.
            if (initialSession.endTime == null) {
                initialSession = initialSession.copy(
                    endTime = Clock.System.now().toEpochMilliseconds()
                )
            }

            _form.value = SessionForm(
                scope = viewModelScope,
                repository = repository,
                toastManager = toastManager,
                initialSession = initialSession,
                dbSessionFlow = repository.getSessionById(sessionId)
            )
        }
    }

    fun saveAndExit(onSuccess: () -> Unit) {
        _form.value?.save(onSuccess)
    }

    fun deleteSession(onSuccess: () -> Unit) {
        _form.value?.delete(onSuccess)
    }
}