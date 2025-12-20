package com.rbmr.timetracker.ui.working

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.ui.components.DateTimeRow
import com.rbmr.timetracker.ui.components.NoteRow
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
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Clock.System.now()
            delay(1000L)
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

                NoteRow(
                    note = session.note,
                    onUpdateNote = onNoteChange
                )

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