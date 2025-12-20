package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.database.WorkSession
import com.rbmr.timetracker.database.WorkSessionDao
import com.rbmr.timetracker.utils.formatDuration
import com.rbmr.timetracker.utils.toUiString
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun EditScreen(
    dao: WorkSessionDao,
    sessionId: Long,
    tempStart: Long?,
    tempEnd: Long?,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var startTime by remember { mutableStateOf(tempStart?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now()) }
    var endTime by remember { mutableStateOf(tempEnd?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now()) }
    var note by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Picker States
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        if (sessionId > 0) {
            val session = dao.getSessionById(sessionId)
            session?.let {
                startTime = Instant.fromEpochMilliseconds(it.startTime)
                endTime = Instant.fromEpochMilliseconds(it.endTime)
                note = it.note
            }
        }
        isLoading = false
    }

    if (isLoading) return

    // --- Pickers ---
    if (showStartPicker) {
        DateTimePicker(
            initialInstant = startTime,
            onConfirm = { startTime = it; showStartPicker = false },
            onDismiss = { showStartPicker = false }
        )
    }
    if (showEndPicker) {
        DateTimePicker(
            initialInstant = endTime,
            onConfirm = { endTime = it; showEndPicker = false },
            onDismiss = { showEndPicker = false }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Total Time: ${formatDuration(startTime, endTime)}", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        // Editable Buttons
        OutlinedButton(
            onClick = { showStartPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start: ${startTime.toUiString()}")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showEndPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("End: ${endTime.toUiString()}")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )

        Spacer(Modifier.weight(1f))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        val session = WorkSession(
                            id = if (sessionId > 0) sessionId else 0,
                            startTime = startTime.toEpochMilliseconds(),
                            endTime = endTime.toEpochMilliseconds(),
                            note = note
                        )
                        if (sessionId > 0) dao.update(session) else dao.insert(session)
                        onNavigateBack()
                    }
                }
            ) { Text("Save") }

            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = {
                    scope.launch {
                        if (sessionId > 0) {
                            dao.delete(WorkSession(id = sessionId, startTime = 0, endTime = 0, note = ""))
                        }
                        onNavigateBack()
                    }
                }
            ) { Text("Delete") }
        }
    }
}