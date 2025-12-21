package com.rbmr.timetracker.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.utils.formatDuration
import com.rbmr.timetracker.utils.toUiString
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    sessions: List<WorkSession>,
    onEdit: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("History") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(sessions) { session ->
                HistoryItem(session, onClick = { onEdit(session.id) })
            }
        }
    }
}

@Composable
fun HistoryItem(session: WorkSession, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = Instant.fromEpochMilliseconds(session.startTime).toUiString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (session.endTime != null) formatDuration(
                        Instant.fromEpochMilliseconds(session.startTime),
                        Instant.fromEpochMilliseconds(session.endTime)
                    ) else "Ongoing",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = session.note.ifEmpty { " " }.replace(Regex("\\s+"), " "),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}