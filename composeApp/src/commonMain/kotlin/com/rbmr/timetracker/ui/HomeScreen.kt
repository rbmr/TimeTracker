package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onPunchIn: () -> Unit, onHistory: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onPunchIn,
            modifier = Modifier.fillMaxWidth(0.6f).padding(16.dp)
        ) {
            Text("Punch In")
        }
        Button(
            onClick = onHistory,
            modifier = Modifier.fillMaxWidth(0.6f).padding(16.dp)
        ) {
            Text("History")
        }
    }
}