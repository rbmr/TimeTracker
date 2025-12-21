package com.rbmr.timetracker.ui.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object Home : Route()
    @Serializable
    data object History : Route()

    @Serializable
    data object Settings : Route()
    @Serializable
    data class Edit(val sessionId: Long) : Route()
}