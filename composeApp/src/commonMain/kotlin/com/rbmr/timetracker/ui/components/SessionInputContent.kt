package com.rbmr.timetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.ui.form.SessionForm
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Shared Form Content for both EditScreen and HomeScreen.
 * Handles Start Time, End Time, and Notes with validation logic wired to the SessionForm.
 */
@Composable
fun SessionInputContent(
    form: SessionForm,
    session: WorkSession,
) {
    val isOngoing = session.endTime == null

    Column(Modifier.fillMaxWidth()) {

        // START TIME
        DateTimeRow(
            label = "Start Time",
            instant = Instant.fromEpochMilliseconds(session.startTime),
            onInstantChange = { newInstant ->
                form.updateField(
                    newValue = newInstant.toEpochMilliseconds(),
                    mutator = { s, v -> s.copy(startTime = v) },
                    validator = { v ->
                        if (!isOngoing) { // Edit
                            when {
                                v > session.endTime -> "Start time cannot be after end time"
                                else -> null
                            }

                        } else { // Home
                            val now = Clock.System.now().toEpochMilliseconds()
                            when {
                                v > now -> "Start time cannot be in the future"
                                else -> null
                            }
                        }
                    },
                    persistImmediately = isOngoing
                )
            }
        )

        // END TIME (Optional)
        if (!isOngoing) {
            DateTimeRow(
                label = "End Time",
                instant = Instant.fromEpochMilliseconds(session.endTime),
                onInstantChange = { newInstant ->
                    form.updateField(
                        newValue = newInstant.toEpochMilliseconds(),
                        mutator = { s, v -> s.copy(endTime = v) },
                        validator = { v ->
                            when {
                                v < session.startTime -> "End time cannot be before start time"
                                else -> null
                            }
                        },
                        persistImmediately = false
                    )
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // NOTE
        NoteRow(
            note = session.note,
            onUpdateNote = { newNote ->
                form.updateField(
                    newValue = newNote,
                    mutator = { s, v -> s.copy(note = v) },
                    persistImmediately = true
                )
            }
        )
    }
}