package com.rbmr.timetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.utils.CsvUtils
import com.rbmr.timetracker.utils.ShareHelper
import com.rbmr.timetracker.utils.ToastManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: WorkSessionRepository,
    private val shareHelper: ShareHelper,
    private val toastManager: ToastManager
) : ViewModel() {

    fun exportDatabase() {
        viewModelScope.launch {
            try {
                // Fetch the current state of the database (one-shot)
                val allSessions = repository.allSessions.first()

                // Convert to CSV
                val csvContent = CsvUtils.sessionsToCsv(allSessions)


                // Trigger Share Sheet
                shareHelper.shareCsv(csvContent)
                toastManager.show("History exported successfully") // Toast
            } catch (e: Exception) {
                toastManager.show("An error occurred during export") // Error Toast
            }
        }
    }

    fun importDatabase(csvContent: String) {
        viewModelScope.launch {
            try {
                // 1. Parse CSV
                val sessions = CsvUtils.csvToSessions(csvContent)

                // 2. Bulk Insert (Upsert)
                if (sessions.isNotEmpty()) {
                    repository.insertAll(sessions)
                    toastManager.show("History imported successfully") // Toast
                } else {
                    toastManager.show("No sessions found to import")
                }
            } catch (e: Exception) {
                println("Import failed: ${e.message}")
                toastManager.show("Import failed") // Error Toast
            }
        }
    }
}