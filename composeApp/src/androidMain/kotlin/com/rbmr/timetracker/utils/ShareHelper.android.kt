package com.rbmr.timetracker.utils

import android.content.Intent
import androidx.core.content.FileProvider
import com.rbmr.timetracker.database.appContext
import java.io.File

class AndroidShareHelper : ShareHelper {
    override fun shareCsv(csvContent: String) {
        val fileName = "timetracker_export.csv"
        val file = File(appContext.cacheDir, fileName)

        file.writeText(csvContent)

        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "text/csv"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(sendIntent, "Export TimeTracker CSV")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(shareIntent)
    }
}

actual fun getShareHelper(): ShareHelper = AndroidShareHelper()