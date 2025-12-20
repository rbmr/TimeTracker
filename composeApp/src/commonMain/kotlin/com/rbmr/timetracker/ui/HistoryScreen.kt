package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.database.WorkSession
import com.rbmr.timetracker.utils.getShareHelper
import com.rbmr.timetracker.utils.toUiString
import kotlin.time.Instant

@Composable
fun HistoryScreen(
    sessions: List<WorkSession>,
    onEdit: (Long) -> Unit,
    onBack: () -> Unit
) {
    val shareHelper = getShareHelper()

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("Back") }
                Text("History", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = {
                    val csv = "Start,End,Note\n" + sessions.joinToString("\n") {
                        val startStr = Instant.fromEpochMilliseconds(it.startTime).toString()
                        val endStr = it.endTime?.let { t -> Instant.fromEpochMilliseconds(t).toString() } ?: "Ongoing"
                        "$startStr,$endStr,${it.note}"
                    }
                    shareHelper.shareCsv(csv)
                }) { Text("Export") }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(sessions) { session ->
                Card(
                    onClick = { onEdit(session.id) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        val startUi = Instant.fromEpochMilliseconds(session.startTime).toUiString()
                        val endUi = session.endTime?.let { Instant.fromEpochMilliseconds(it).toUiString() } ?: "Ongoing"

                        Text("$startUi -> $endUi", style = MaterialTheme.typography.bodyMedium)
                        if(session.note.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = session.note.take(50),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}