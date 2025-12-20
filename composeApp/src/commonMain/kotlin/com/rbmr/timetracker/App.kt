package com.rbmr.timetracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.data.database.getDatabaseBuilder
import com.rbmr.timetracker.ui.navigation.Route
import com.rbmr.timetracker.ui.edit.EditScreen
import com.rbmr.timetracker.ui.edit.EditViewModel
import com.rbmr.timetracker.ui.history.HistoryScreen
import com.rbmr.timetracker.ui.history.HistoryViewModel
import com.rbmr.timetracker.ui.home.HomeScreen
import com.rbmr.timetracker.ui.home.HomeViewModel
import com.rbmr.timetracker.ui.working.WorkingScreen
import com.rbmr.timetracker.ui.working.WorkingViewModel
import com.rbmr.timetracker.utils.getShareHelper

@Composable
fun App() {
    MaterialTheme {
        // Dependency Injection (Simplified Manual DI)
        // ideally, these singleton instances would live in a real DI container
        val db = remember {
            getDatabaseBuilder()
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(true)
                .build()
        }
        val repository = remember { WorkSessionRepository(db.workSessionDao()) }
        val shareHelper = remember { getShareHelper() }

        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Route.Home) {

            // HOME
            composable<Route.Home> {
                val viewModel = viewModel { HomeViewModel(repository) }
                val ongoingSession by viewModel.ongoingSession.collectAsState()

                HomeScreen(
                    ongoingSession = ongoingSession,
                    onPunchIn = {
                        viewModel.onPunchIn { navController.navigate(Route.Working) }
                    },
                    onHistory = { navController.navigate(Route.History) }
                )
            }

            // WORKING
            composable<Route.Working> {
                val viewModel = viewModel { WorkingViewModel(repository) }
                val session by viewModel.session.collectAsState()

                WorkingScreen(
                    session = session,
                    onNoteChange = viewModel::onNoteChange,
                    onUpdateStartTime = viewModel::onUpdateStartTime,
                    onPunchOut = { id -> navController.navigate(Route.Edit(id)) },
                    onBack = {
                        navController.navigate(Route.Home) { popUpTo(Route.Home) { inclusive = true } }
                    },
                    onDelete = {
                        viewModel.onDeleteSession {
                            navController.navigate(Route.Home) { popUpTo(Route.Home) { inclusive = true } }
                        }
                    }
                )
            }

            // HISTORY
            composable<Route.History> {
                val viewModel = viewModel { HistoryViewModel(repository, shareHelper) }
                val sessions by viewModel.historicalSessions.collectAsState()

                HistoryScreen(
                    sessions = sessions,
                    onEdit = { id -> navController.navigate(Route.Edit(id)) },
                    onExport = { viewModel.exportHistory(sessions) },
                    onBack = {
                        navController.navigate(Route.Home) { popUpTo(Route.Home) { inclusive = true } }
                    }
                )
            }

            // EDIT
            composable<Route.Edit> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.Edit>()
                val viewModel = viewModel { EditViewModel(repository, route.sessionId) }
                val session by viewModel.sessionState.collectAsState()

                if (session != null) {
                    val isOngoing = session!!.endTime == null

                    EditScreen(
                        session = session!!,
                        onUpdateSession = viewModel::updateSession,
                        onSaveAndExit = { endTime ->
                            viewModel.saveAndExit(session!!, endTime) {
                                if (isOngoing) {
                                    navController.navigate(Route.Home) {
                                        popUpTo(Route.Home) { inclusive = true }
                                    }
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        },
                        onBack = { navController.popBackStack() },
                        onDelete = {
                            viewModel.deleteSession {
                                if (isOngoing) {
                                    navController.navigate(Route.Home) {
                                        popUpTo(Route.Home) { inclusive = true }
                                    }
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        }
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}