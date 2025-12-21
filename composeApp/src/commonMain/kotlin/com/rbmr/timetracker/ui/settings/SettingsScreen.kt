package com.rbmr.timetracker.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onExport,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Export History to CSV")
            }

            OutlinedButton(
                onClick = onImport,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Import History from CSV")
            }
        }
    }
}