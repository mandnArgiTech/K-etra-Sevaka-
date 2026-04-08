package com.ksetrasevakah.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.feature.hub.HubScreen
import com.ksetrasevakah.feature.pumpiq.dashboard.DashboardScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    KsetraTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.Hub.route
        ) {
            composable(Screen.Hub.route) {
                HubScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChat = { query ->
                        navController.navigate(Screen.Chat.createRoute(query))
                    }
                )
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(
                    navArgument("initialQuery") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    }
                )
            ) {
                // Chat screen placeholder — will be implemented in E08
            }

            composable(Screen.Settings.route) {
                // Settings screen placeholder — will be implemented in E11
            }
        }
    }
}
