package com.rbmr.timetracker.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun formatDuration(start: Instant, end: Instant): String {
    val duration = end - start
    val totalSeconds = duration.inWholeSeconds
    val hours = (totalSeconds / 3600).coerceAtMost(99)
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val hoursStr = hours.toString().padStart(2, '0')
    val minutesStr = minutes.toString().padStart(2, '0')
    val secondsStr = seconds.toString().padStart(2, '0')
    return "${hoursStr}:${minutesStr}:${secondsStr}"
}

fun Instant.toUiString(): String {
    val local = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val hourStr = local.hour.toString().padStart(2,'0')
    val minuteStr = local.minute.toString().padStart(2,'0')
    val secondsStr = local.second.toString().padStart(2, '0')
    return "${local.date} ${hourStr}:${minuteStr}:${secondsStr}"
}