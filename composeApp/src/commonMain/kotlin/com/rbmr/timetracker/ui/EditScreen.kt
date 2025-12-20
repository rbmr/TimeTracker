package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.database.WorkSession
import com.rbmr.timetracker.ui.components.DateTimeRow
import com.rbmr.timetracker.utils.formatDuration
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun EditScreen(
    session: WorkSession,
    onUpdateSession: (WorkSession) -> Unit,
    onSaveAndExit: () -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    // Determine if this is a "Punch Out" scenario (Active session)
    val isOngoing = remember { session.endTime == null }

    // Form State (initialized from passed session)
    var startTime by remember { mutableStateOf(Instant.fromEpochMilliseconds(session.startTime)) }

    // If ongoing, default End Time to Now (for display/picking), but don't save it yet.
    var endTime by remember {
        mutableStateOf(
            if (session.endTime != null) Instant.fromEpochMilliseconds(session.endTime)
            else Clock.System.now()
        )
    }
    var note by remember { mutableStateOf(session.note) }

    // Helper to build the object based on current form state
    fun createSession(forceEndTime: Boolean): WorkSession {
        return session.copy(
            startTime = startTime.toEpochMilliseconds(),
            // Only write end time if forced (Save clicked) or if it was already historical
            endTime = if (forceEndTime || !isOngoing) endTime.toEpochMilliseconds() else null,
            note = note
        )
    }

    // Auto-save Fields (React to changes)
    LaunchedEffect(startTime, note) {
        onUpdateSession(createSession(forceEndTime = false))
    }

    // If we change End Time on a historical session, save it immediately.
    LaunchedEffect(endTime) {
        if (!isOngoing) {
            onUpdateSession(createSession(forceEndTime = false))
        }
    }

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("Back") }
                Text("Edit Session", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
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
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // Force Save the End Time (Punch Out)
                    onUpdateSession(createSession(forceEndTime = true))
                    onSaveAndExit()
                }
            ) { Text("Save") }
        }
    }
}