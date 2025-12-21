package com.rbmr.timetracker.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.utils.formatDuration
import com.rbmr.timetracker.utils.toUiString
import kotlin.time.Instant

@Composable
fun HistoryScreen(
    sessions: List<WorkSession>,
    onEdit: (Long) -> Unit,
    onExport: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("Back") }
                Text("History", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onExport) { Text("Export") }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(sessions) { session ->
                Card(
                    onClick = { onEdit(session.id) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // ROW 1: Start Time & Duration ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Start Time (Left)
                            Text(
                                text = Instant.fromEpochMilliseconds(session.startTime).toUiString(),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Duration (Right)
                            val durationText = if (session.endTime != null) {
                                formatDuration(
                                    Instant.fromEpochMilliseconds(session.startTime),
                                    Instant.fromEpochMilliseconds(session.endTime)
                                )
                            } else {
                                "Ongoing"
                            }

                            Text(
                                text = durationText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // ROW 2: Note (Single Line, Cropped)
                        // We use a space " " if empty to ensure the row keeps its height
                        val noteContent = session.note.ifEmpty { " " }
                        // Flatten newlines to spaces for the preview
                        val singleLineNote = noteContent.replace(Regex("\\s+"), " ")

                        Text(
                            text = singleLineNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}