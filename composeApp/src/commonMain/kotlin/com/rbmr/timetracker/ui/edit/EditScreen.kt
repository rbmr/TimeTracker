package com.rbmr.timetracker.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.ui.components.DateTimeRow
import com.rbmr.timetracker.utils.formatDuration
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun EditScreen(
    session: WorkSession,
    onUpdateSession: (WorkSession) -> Unit,
    onSaveAndExit: (Long) -> Unit, // Pass end time
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    val isOngoing = remember { session.endTime == null }

    // Local State for immediate UI feedback
    var startTime by remember { mutableStateOf(Instant.fromEpochMilliseconds(session.startTime)) }
    var endTime by remember {
        mutableStateOf(
            if (session.endTime != null) Instant.fromEpochMilliseconds(session.endTime)
            else Clock.System.now()
        )
    }
    var note by remember { mutableStateOf(session.note) }

    // Helper to sync local changes to DB immediately (for historical edits)
    fun persistChanges() {
        val updated = session.copy(
            startTime = startTime.toEpochMilliseconds(),
            // If it was already historical, we save the end time.
            // If it's ongoing, we leave it null until "Save" is clicked.
            endTime = if (!isOngoing) endTime.toEpochMilliseconds() else null,
            note = note
        )
        onUpdateSession(updated)
    }

    // Auto-save effects (Debouncing could be added here or in VM, simplified here)
    LaunchedEffect(startTime, note) { persistChanges() }

    // Only auto-save endTime if it's NOT an ongoing session (Punch out hasn't happened yet)
    LaunchedEffect(endTime) {
        if (!isOngoing) persistChanges()
    }

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("Back") }
                Text(if (isOngoing) "Punch Out" else "Edit Session", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Total Time: ${formatDuration(startTime, endTime)}",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(24.dp))
            DateTimeRow("Start Time", startTime) { startTime = it }
            DateTimeRow("End Time", endTime) { endTime = it }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(Modifier.weight(1f))

            // The "Save" button acts as the confirmation for Punch Out
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSaveAndExit(endTime.toEpochMilliseconds())
                }
            ) { Text("Save") }
        }
    }
}