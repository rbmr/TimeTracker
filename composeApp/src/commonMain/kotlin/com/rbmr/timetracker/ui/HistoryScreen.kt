package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.database.WorkSessionDao
import com.rbmr.timetracker.utils.getShareHelper
import com.rbmr.timetracker.utils.toUiString
import kotlin.time.Instant

@Composable
fun HistoryScreen(dao: WorkSessionDao, onEdit: (Long) -> Unit, onBack: () -> Unit) {
    val sessions by dao.getAllSessions().collectAsState(initial = emptyList())
    val shareHelper = getShareHelper()

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("History", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = {
                val csv = "Start,End,Note\n" + sessions.joinToString("\n") {
                    "${Instant.fromEpochMilliseconds(it.startTime)},${Instant.fromEpochMilliseconds(it.endTime)},${it.note}"
                }
                shareHelper.shareCsv(csv)
            }) { Text("Export CSV") }
        }

        LazyColumn {
            items(sessions) { session ->
                Card(
                    onClick = { onEdit(session.id) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "${Instant.fromEpochMilliseconds(session.startTime).toUiString()} -> ${Instant.fromEpochMilliseconds(session.endTime).toUiString()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
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