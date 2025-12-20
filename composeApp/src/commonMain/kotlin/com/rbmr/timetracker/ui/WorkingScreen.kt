package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.database.WorkSession
import com.rbmr.timetracker.ui.components.DateTimeRow
import com.rbmr.timetracker.utils.formatDuration
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun WorkingScreen(
    session: WorkSession?,
    onUpdateSession: (WorkSession) -> Unit,
    onPunchOut: (Long) -> Unit,
    onBack: () -> Unit,
    onDelete: (WorkSession) -> Unit
) {
    // Local UI State (The clock ticking)
    var currentTime by remember { mutableStateOf(Clock.System.now()) }
    // Local Note State (for smooth typing)
    var note by remember(session) { mutableStateOf(session?.note ?: "") }
    var showNoteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Clock.System.now()
            delay(1000L)
        }
    }

    // Auto-save note when user stops typing (simplified: save on change)
    LaunchedEffect(note) {
        if (session != null && note != session.note) {
            onUpdateSession(session.copy(note = note))
        }
    }

    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Edit Note") },
            text = {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = { TextButton(onClick = { showNoteDialog = false }) { Text("Done") } }
        )
    }

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("Back") }
                Text("Working", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = { session?.let { onDelete(it) } },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            if (session != null) {
                val startInstant = Instant.fromEpochMilliseconds(session.startTime)

                Text(
                    text = formatDuration(startInstant, currentTime),
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(Modifier.height(32.dp))

                DateTimeRow("Start Time", startInstant) { newStart ->
                    onUpdateSession(session.copy(startTime = newStart.toEpochMilliseconds()))
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showNoteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (note.isEmpty()) "Add Note" else "Edit Note")
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { onPunchOut(session.id) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Punch Out")
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }
}