package com.rbmr.timetracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rbmr.timetracker.database.getDatabaseBuilder
import com.rbmr.timetracker.ui.EditScreen
import com.rbmr.timetracker.ui.HistoryScreen
import com.rbmr.timetracker.ui.HomeScreen
import com.rbmr.timetracker.ui.Route
import com.rbmr.timetracker.ui.WorkingScreen

@Composable
fun App() {
    MaterialTheme {
        // Initialize Database
        val db = remember {
            getDatabaseBuilder()
                .setDriver(BundledSQLiteDriver())
                .build()
        }
        val dao = db.workSessionDao()
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Route.Home) {
            composable<Route.Home> {
                HomeScreen(
                    onPunchIn = { navController.navigate(Route.Working) },
                    onHistory = { navController.navigate(Route.History) }
                )
            }

            composable<Route.Working> {
                WorkingScreen(
                    onPunchOut = { start, end ->
                        navController.navigate(Route.Edit(sessionId = 0, tempStart = start, tempEnd = end))
                    }
                )
            }

            composable<Route.History> {
                HistoryScreen(
                    dao = dao,
                    onEdit = { id -> navController.navigate(Route.Edit(sessionId = id)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Route.Edit> { backStackEntry ->
                val route: Route.Edit = backStackEntry.toRoute()
                EditScreen(
                    dao = dao,
                    sessionId = route.sessionId,
                    tempStart = route.tempStart,
                    tempEnd = route.tempEnd,
                    onNavigateBack = {
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}