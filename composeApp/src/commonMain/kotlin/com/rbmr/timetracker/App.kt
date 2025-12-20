package com.rbmr.timetracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rbmr.timetracker.database.WorkSession
import com.rbmr.timetracker.database.getDatabaseBuilder
import com.rbmr.timetracker.ui.*
import kotlinx.coroutines.launch
import kotlin.time.Clock

@Composable
fun App() {
    MaterialTheme {
        // Initialization
        val db = remember {
            getDatabaseBuilder()
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(true)
                .build()
        }
        val dao = db.workSessionDao()
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()

        // Navigation Controller (Shows the Screens)
        NavHost(navController = navController, startDestination = Route.Home) {

            // HOME SCREEN
            composable<Route.Home> {
                val ongoingSession by dao.getOngoingSessionFlow().collectAsState(initial = null)

                HomeScreen(
                    ongoingSessionExists = (ongoingSession != null),
                    onPunchIn = {
                        if (ongoingSession != null) {
                            // Resume existing
                            navController.navigate(Route.Working)
                        } else {
                            // Create new, then go to Working
                            scope.launch {
                                val newSession = WorkSession(
                                    startTime = Clock.System.now().toEpochMilliseconds(),
                                    endTime = null,
                                    note = ""
                                )
                                dao.insert(newSession)
                                navController.navigate(Route.Working)
                            }
                        }
                    },
                    onHistory = { navController.navigate(Route.History) }
                )
            }

            // WORKING SCREEN
            composable<Route.Working> {
                val ongoingSession by dao.getOngoingSessionFlow().collectAsState(initial = null)

                WorkingScreen(
                    session = ongoingSession,
                    onUpdateSession = { updated -> scope.launch { dao.update(updated) } },
                    onPunchOut = { id -> navController.navigate(Route.Edit(id)) },
                    onBack = {
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
                        }
                    },
                    onDelete = { sessionToDelete ->
                        scope.launch {
                            dao.delete(sessionToDelete)
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Home) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // HISTORY SCREEN
            composable<Route.History> {
                val historySessions by dao.getHistoricalSessionsFlow().collectAsState(initial = emptyList())

                HistoryScreen(
                    sessions = historySessions,
                    onEdit = { id -> navController.navigate(Route.Edit(id)) },
                    onBack = {
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
                        }
                    }
                )
            }

            // EDIT SCREEN
            composable<Route.Edit> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.Edit>()
                val id = route.sessionId
                val session by dao.getSessionByIdFlow(id).collectAsState(initial = null)

                // A. Resolve Data: Find the object in our global lists
                val isOngoing = remember(session?.id) {
                    session?.endTime == null
                }

                // B. Render
                if (session != null) {
                    EditScreen(
                        session = session!!,
                        onUpdateSession = { updated -> scope.launch { dao.update(updated) } },
                        onSaveAndExit = {
                            if (isOngoing) {
                                // Working -> PunchOut -> Save -> Home
                                navController.navigate(Route.Home) {
                                    popUpTo(Route.Home) { inclusive = true }
                                }
                            } else {
                                // History -> Edit -> Save -> History
                                navController.popBackStack()
                            }
                        },
                        onBack = {
                            if (isOngoing) {
                                // Working -> PunchOut -> Back -> Working (Cancel punch out)
                                navController.popBackStack()
                            } else {
                                // History -> Edit -> Back -> History
                                navController.popBackStack()
                            }
                        },
                        onDelete = {
                            scope.launch {
                                dao.delete(session!!)
                                if (isOngoing) {
                                    // Working -> PunchOut -> Delete -> Home
                                    navController.navigate(Route.Home) {
                                        popUpTo(Route.Home) { inclusive = true }
                                    }
                                } else {
                                    // History -> Edit -> Delete -> History
                                    navController.popBackStack()
                                }
                            }
                        }
                    )
                } else {
                    // Loading or ID not found
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}