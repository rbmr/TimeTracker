package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.utils.formatDuration
import com.rbmr.timetracker.utils.toUiString
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun WorkingScreen(onPunchOut: (start: Long, end: Long) -> Unit) {
    var startTime by remember { mutableStateOf<Instant>(Clock.System.now()) }
    var currentTime by remember { mutableStateOf<Instant>(Clock.System.now()) }
    var showStartTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Clock.System.now()
            delay(1000L)
        }
    }

    // Logic for DateTime Picker
    if (showStartTimePicker) {
        DateTimePicker(
            initialInstant = startTime,
            onConfirm = { newStart ->
                startTime = newStart
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = formatDuration(startTime, currentTime),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(Modifier.height(32.dp))

        // Editable Start Time
        OutlinedButton(onClick = { showStartTimePicker = true }) {
            Text("Start: ${startTime.toUiString()}")
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                onPunchOut(
                    startTime.toEpochMilliseconds(),
                    currentTime.toEpochMilliseconds()
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Punch Out")
        }
    }
}