package com.rbmr.timetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.utils.CsvUtils
import com.rbmr.timetracker.utils.ShareHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: WorkSessionRepository,
    private val shareHelper: ShareHelper
) : ViewModel() {

    fun exportDatabase() {
        viewModelScope.launch {
            // Fetch the current state of the database (one-shot)
            val allSessions = repository.allSessions.first()

            // Convert to CSV
            val csvContent = CsvUtils.sessionsToCsv(allSessions)

            // Trigger Share Sheet
            shareHelper.shareCsv(csvContent)
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
                }
            } catch (e: Exception) {
                // TODO: Handle error (e.g., expose an error state to UI)
                println("Import failed: ${e.message}")
            }
        }
    }
}