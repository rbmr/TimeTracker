package com.rbmr.timetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeRow(
    label: String,
    instant: Instant,
    onInstantChange: (Instant) -> Unit
) {
    val systemTZ = TimeZone.currentSystemDefault()
    val localDateTime = instant.toLocalDateTime(systemTZ)

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        // 1. Small label on top
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // 2. Date and Time Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DATE BLOCK
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDatePicker = true },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = localDateTime.date.toString(),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // TIME BLOCK
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showTimePicker = true },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                val hour = localDateTime.hour.toString().padStart(2, '0')
                val minute = localDateTime.minute.toString().padStart(2, '0')
                val second = localDateTime.second.toString().padStart(2, '0')
                Text(
                    text = "$hour:$minute:$second",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    // LOGIC FOR INDEPENDENT PICKERS

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = instant.toEpochMilliseconds()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Combine new Date with OLD Time
                        val newDate = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date

                        val newLocalDateTime = LocalDateTime(newDate, localDateTime.time)
                        onInstantChange(newLocalDateTime.toInstant(systemTZ))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = localDateTime.hour,
            initialMinute = localDateTime.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Combine OLD Date with NEW Time
                    val newTime = LocalTime(timePickerState.hour, timePickerState.minute, 0)
                    val newLocalDateTime = LocalDateTime(localDateTime.date, newTime)
                    onInstantChange(newLocalDateTime.toInstant(systemTZ))
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}