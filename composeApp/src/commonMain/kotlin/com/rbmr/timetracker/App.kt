package com.rbmr.timetracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rbmr.timetracker.data.database.getDatabaseBuilder
import com.rbmr.timetracker.data.repository.WorkSessionRepository
import com.rbmr.timetracker.ui.edit.EditScreen
import com.rbmr.timetracker.ui.edit.EditViewModel
import com.rbmr.timetracker.ui.history.HistoryScreen
import com.rbmr.timetracker.ui.history.HistoryViewModel
import com.rbmr.timetracker.ui.home.HomeScreen
import com.rbmr.timetracker.ui.home.HomeViewModel
import com.rbmr.timetracker.ui.navigation.Route
import com.rbmr.timetracker.ui.settings.SettingsScreen
import com.rbmr.timetracker.ui.settings.SettingsViewModel
import com.rbmr.timetracker.utils.ToastManager
import com.rbmr.timetracker.utils.getShareHelper


@Composable
fun App() {
    MaterialTheme {
        val db = remember {
            getDatabaseBuilder()
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(true)
                .build()
        }
        val repository = remember { WorkSessionRepository(db.workSessionDao()) }
        val shareHelper = remember { getShareHelper() }

        val toastManager = remember { ToastManager() }
        val snackbarHostState = remember { SnackbarHostState() }

        val navController = rememberNavController()

        // Determine if we should show the bottom bar (Hide on Edit)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val showBottomBar = currentDestination?.hasRoute<Route.Edit>() == false

        // Listen for messages and show Snackbars
        LaunchedEffect(Unit) {
            toastManager.messages.collect { message ->
                snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            }
        }


        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.DateRange, "History") },
                            label = { Text("History") },
                            selected = currentDestination.hasRoute<Route.History>(),
                            onClick = {
                                navController.navigate(Route.History) {
                                    popUpTo(Route.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, "Home") },
                            label = { Text("Home") },
                            selected = currentDestination.hasRoute<Route.Home>(),
                            onClick = {
                                navController.navigate(Route.Home) {
                                    popUpTo(Route.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, "Settings") },
                            label = { Text("Settings") },
                            selected = currentDestination.hasRoute<Route.Settings>(),
                            onClick = {
                                navController.navigate(Route.Settings) {
                                    popUpTo(Route.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home,
                modifier = Modifier.padding(innerPadding)
            ) {
                // HOME
                composable<Route.Home> {
                    val viewModel = viewModel { HomeViewModel(repository, toastManager) }
                    val ongoingSession by viewModel.ongoingSession.collectAsState()

                    HomeScreen(
                        ongoingSession = ongoingSession,
                        onPunchIn = viewModel::onPunchIn,
                        onPunchOut = { id -> navController.navigate(Route.Edit(id)) },
                        onNoteChange = viewModel::onNoteChange,
                        onUpdateStartTime = viewModel::onUpdateStartTime,
                        onDelete = viewModel::onDeleteSession,
                    )
                }

                // HISTORY
                composable<Route.History> {
                    val viewModel = viewModel { HistoryViewModel(repository) }
                    val sessions by viewModel.historicalSessions.collectAsState()
                    HistoryScreen(
                        sessions = sessions,
                        onEdit = { id -> navController.navigate(Route.Edit(id)) }
                    )
                }

                // SETTINGS
                composable<Route.Settings> {
                    val viewModel = viewModel { SettingsViewModel(repository, shareHelper, toastManager) }
                    SettingsScreen(
                        onExport = { viewModel.exportDatabase() },
                        onImport = { csvContent -> viewModel.importDatabase(csvContent) }
                    )
                }

                // EDIT (Popup-like)
                composable<Route.Edit> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.Edit>()
                    val viewModel = viewModel { EditViewModel(repository, toastManager, route.sessionId) }
                    val session by viewModel.sessionState.collectAsState()

                    if (session != null) {
                        val isOngoing = session!!.endTime == null

                        EditScreen(
                            session = session!!,
                            onUpdateSession = viewModel::updateSession,
                            onSaveAndExit = { endTime ->
                                viewModel.saveAndExit(session!!, endTime) {
                                    navController.popBackStack()
                                }
                            },
                            onBack = { navController.popBackStack() },
                            onDelete = {
                                viewModel.deleteSession {
                                    navController.popBackStack()
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
}