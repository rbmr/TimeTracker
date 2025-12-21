package com.rbmr.timetracker.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFileImporter(onResult: (String?) -> Unit): FileImporterLauncher {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val content = inputStream.reader().readText()
                    onResult(content)
                } ?: onResult(null)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        } else {
            onResult(null)
        }
    }

    return remember {
        object : FileImporterLauncher {
            override fun launch() {
                // Filter for common text/csv types
                launcher.launch(arrayOf("text/*", "application/csv", "application/octet-stream"))
            }
        }
    }
}