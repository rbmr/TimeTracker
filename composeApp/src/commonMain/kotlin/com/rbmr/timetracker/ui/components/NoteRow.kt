package com.rbmr.timetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun NoteRow(
    note: String,
    onUpdateNote: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    // Temporary state for the dialog input
    var tempNote by remember { mutableStateOf("") }

    // Logic: Flatten whitespace to single spaces for the preview
    val previewText = remember(note) {
        if (note.isEmpty()) "Tap to add note" // Placeholder
        else note.replace(Regex("\\s+"), " ")
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        // Small Label
        Text(
            text = "Note",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Preview Field (Acts as Button)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    tempNote = note
                    showDialog = true
                },
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = previewText,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                softWrap = false
            )
        }
    }

    // Popup Window
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text("Edit Note") },
            text = {
                OutlinedTextField(
                    value = tempNote,
                    onValueChange = { tempNote = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    placeholder = { Text("Enter your note here...") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateNote(tempNote)
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}