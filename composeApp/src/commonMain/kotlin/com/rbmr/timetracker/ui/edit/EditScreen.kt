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
import com.rbmr.timetracker.ui.components.SessionInputContent
import com.rbmr.timetracker.ui.form.SessionForm
import com.rbmr.timetracker.utils.formatDuration
import kotlin.time.Instant

@Composable
fun EditScreen(
    form: SessionForm,
    session: WorkSession,
    onSaveAndExit: () -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
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
            Modifier.fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Total Duration
            val endInstant = session.endTime?.let { Instant.fromEpochMilliseconds(it) }
            Text(
                text = "Total Time: " + if (endInstant != null) {
                    formatDuration(Instant.fromEpochMilliseconds(session.startTime), endInstant)
                } else {
                    "Ongoing"
                },
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(24.dp))

            // Shared Input Content
            SessionInputContent(form = form, session = session)

            Spacer(Modifier.weight(1f))

            // Save Button (Primary action for historical edits)
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSaveAndExit
            ) { Text("Save") }
        }
    }
}