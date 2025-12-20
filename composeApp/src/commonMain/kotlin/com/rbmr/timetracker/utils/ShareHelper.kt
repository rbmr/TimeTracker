package com.rbmr.timetracker.utils

interface ShareHelper {
    fun shareCsv(csvContent: String)
}

expect fun getShareHelper(): ShareHelper