package com.rbmr.timetracker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.ui.components.DateTimeRow
import com.rbmr.timetracker.ui.components.SessionInputContent
import com.rbmr.timetracker.ui.form.SessionForm
import com.rbmr.timetracker.utils.formatDuration
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    form: SessionForm?,
    session: WorkSession?,
    onPunchIn: () -> Unit,
    onPunchOut: (Long) -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Home") })
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (form == null || session == null) {
                // STATE 1: IDLE
                Button(
                    onClick = onPunchIn,
                    modifier = Modifier.size(200.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Punch In", style = MaterialTheme.typography.headlineMedium)
                }
            } else {
                // STATE 2: WORKING
                WorkingContent(
                    form = form,
                    session = session,
                    onPunchOut = { onPunchOut(session.id) },
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun WorkingContent(
    form: SessionForm,
    session: WorkSession,
    onPunchOut: () -> Unit,
    onDelete: () -> Unit
) {
    // Ticking Clock
    var currentTime by remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Clock.System.now()
            delay(1000L)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // Timer
        Text(
            text = formatDuration(Instant.fromEpochMilliseconds(session.startTime), currentTime),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(Modifier.height(32.dp))

        SessionInputContent(form = form, session = session)

        Spacer(Modifier.weight(1f))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Delete
            OutlinedButton(
                onClick = onDelete,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f)
            ) { Text("Delete") }

            // Punch Out (Navigates to Edit)
            Button(
                onClick = onPunchOut,
                modifier = Modifier.weight(2f)
            ) { Text("Punch Out") }
        }
    }
}