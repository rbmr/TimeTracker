package com.rbmr.timetracker.utils

import androidx.compose.runtime.Composable

interface FileImporterLauncher {
    fun launch()
}

@Composable
expect fun rememberFileImporter(onResult: (String?) -> Unit): FileImporterLauncher