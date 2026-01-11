package com.rbmr.timetracker.ui.form

import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.utils.ToastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SessionForm(
    private val scope: CoroutineScope,
    private val repository: WorkSessionRepository,
    private val toastManager: ToastManager,
    initialSession: WorkSession,
    dbSessionFlow: Flow<WorkSession?>
) {
    // UI State (What the user sees)
    private val _uiSession = MutableStateFlow(initialSession)
    val uiSession = _uiSession.asStateFlow()

    // DB State (What is currently saved)
    private val _dbSession = MutableStateFlow(initialSession)

    init {
        scope.launch {
            dbSessionFlow.filterNotNull().collect {
                _dbSession.value = it
            }
        }
    }

    /**
     * Generic update method for any field.
     * @param newValue The new value for the field.
     * @param mutator Function to apply the new value to a WorkSession.
     * @param validator Optional function that returns an error string if invalid, or null if valid.
     * @param persistImmediately If true, writes this specific change to the DB immediately.
     */
    fun <T> updateField(
        newValue: T,
        mutator: (WorkSession, T) -> WorkSession,
        validator: ((T) -> String?)? = null,
        persistImmediately: Boolean = false
    ) {
        // Validate
        val error = validator?.invoke(newValue)
        if (error != null) {
            toastManager.show(error)
            return
        }

        // Update UI State
        _uiSession.update { mutator(it, newValue) }

        // Persist to DB if requested
        if (persistImmediately) {
            val sessionToSave = mutator(_dbSession.value, newValue)
            scope.launch {
                try {
                    repository.update(sessionToSave)
                    toastManager.show("Updated session")
                } catch (e: Exception) {
                    toastManager.show("Error updating session")
                }
            }
        }
    }

    /**
     * Persists the entire UI state to the Database.
     * Only performs write if there are actual differences.
     */
    fun save(onSuccess: () -> Unit = {}) {
        val currentUi = _uiSession.value
        val currentDb = _dbSession.value

        if (currentUi == currentDb) {
            onSuccess()
            return
        }

        scope.launch {
            try {
                repository.update(currentUi)
                toastManager.show("Saved session")
                onSuccess()
            } catch (e: Exception) {
                toastManager.show("Error saving session")
            }
        }
    }

    fun delete(onSuccess: () -> Unit = {}) {
        scope.launch {
            try {
                repository.delete(_dbSession.value)
                toastManager.show("Deleted session")
                onSuccess()
            } catch (e: Exception) {
                toastManager.show("Error deleting session")
            }
        }
    }

    fun hasUnsavedChanges(): Boolean {
        return _uiSession.value != _dbSession.value
    }
}