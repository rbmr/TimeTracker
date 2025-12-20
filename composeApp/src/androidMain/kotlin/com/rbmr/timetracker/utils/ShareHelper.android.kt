package com.rbmr.timetracker.utils

import android.content.Intent
import com.rbmr.timetracker.database.appContext

class AndroidShareHelper : ShareHelper {
    override fun shareCsv(csvContent: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csvContent)
            type = "text/csv"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export TimeTracker CSV")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(shareIntent)
    }
}

actual fun getShareHelper(): ShareHelper = AndroidShareHelper()