package com.rbmr.timetracker.ui.working

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.ui.components.DateTimeRow
import com.rbmr.timetracker.utils.formatDuration
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun WorkingScreen(
    session: WorkSession?,
    onNoteChange: (String) -> Unit,
    onUpdateStartTime: (Long) -> Unit,
    onPunchOut: (Long) -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    // Local UI State for the ticking clock only
    var currentTime by remember { mutableStateOf(Clock.System.now()) }
    var showNoteDialog by remember { mutableStateOf(false) }

    // Sync local note state with DB session initially
    // We use a local state to drive the text field immediately
    var currentNote by remember(session?.note) { mutableStateOf(session?.note ?: "") }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Clock.System.now()
            delay(1000L)
        }
    }

    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Edit Note") },
            text = {
                OutlinedTextField(
                    value = currentNote,
                    onValueChange = {
                        currentNote = it
                        onNoteChange(it) // Send to VM to debounce/save
                    },
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
                    onClick = onDelete,
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
                    onUpdateStartTime(newStart.toEpochMilliseconds())
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showNoteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (currentNote.isEmpty()) "Add Note" else "Edit Note")
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