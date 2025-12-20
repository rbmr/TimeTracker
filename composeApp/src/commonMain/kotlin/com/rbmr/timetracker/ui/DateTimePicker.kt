package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    initialInstant: kotlin.time.Instant,
    onConfirm: (kotlin.time.Instant) -> Unit,
    onDismiss: () -> Unit
) {
    val systemTZ = TimeZone.currentSystemDefault()
    // Convert Instant to local date components for the pickers
    val initialLocal = initialInstant.toLocalDateTime(systemTZ)

    // State to track which dialog is open
    var showTimePicker by remember { mutableStateOf(false) }

    // Store the date selected in step 1
    var selectedDate by remember { mutableStateOf(initialLocal.date) }

    // --- STEP 1: Date Picker Dialog ---
    if (!showTimePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialInstant.toEpochMilliseconds()
        )

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date // DatePicker uses UTC millis
                        selectedDate = localDate
                        showTimePicker = true // Move to Step 2
                    }
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    // --- STEP 2: Time Picker Dialog ---
    else {
        val timePickerState = rememberTimePickerState(
            initialHour = initialLocal.hour,
            initialMinute = initialLocal.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    // Combine Date + Time -> Instant
                    val newLocalDateTime = LocalDateTime(
                        date = selectedDate,
                        time = LocalTime(timePickerState.hour, timePickerState.minute, 0)
                    )
                    val newInstant = newLocalDateTime.toInstant(systemTZ)
                    onConfirm(newInstant)
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Back") }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Pick Time", style = MaterialTheme.typography.titleMedium)
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}