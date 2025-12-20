package com.rbmr.timetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    ongoingSessionExists: Boolean,
    onPunchIn: () -> Unit,
    onHistory: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onPunchIn,
            modifier = Modifier.fillMaxWidth(0.7f).defaultMinSize(minHeight = 72.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = if (ongoingSessionExists) "Resume" else "Punch In",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onHistory,
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray.copy(alpha = 0.5f),
                contentColor = Color.White
            )
        ) {
            Text("History")
        }
    }
}