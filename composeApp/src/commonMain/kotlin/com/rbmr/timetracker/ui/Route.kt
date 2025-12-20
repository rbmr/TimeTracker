package com.rbmr.timetracker.ui

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object Home : Route()
    @Serializable
    data object Working : Route()
    @Serializable
    data object History : Route()
    @Serializable
    data class Edit(val sessionId: Long, val tempStart: Long? = null, val tempEnd: Long? = null) : Route()
}