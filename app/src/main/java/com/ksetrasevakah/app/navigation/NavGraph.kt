package com.ksetrasevakah.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.feature.hub.HubScreen
import com.ksetrasevakah.feature.pumpiq.chat.ChatScreen
import com.ksetrasevakah.feature.pumpiq.dashboard.DashboardScreen
import com.ksetrasevakah.feature.bootdiagnostics.BootDiagnosticsScreen
import com.ksetrasevakah.feature.connectivity.ConnectivityCheckScreen
import com.ksetrasevakah.feature.diagnostics.DataDiagnosticsScreen
import com.ksetrasevakah.feature.settings.SettingsScreen
import com.ksetrasevakah.feature.setup.SetupScreen
import com.ksetrasevakah.feature.suraksha.camera.CameraMatrixScreen
import com.ksetrasevakah.feature.suraksha.dashboard.SurakshaDashboardScreen

@Composable
fun NavGraph() {
    val rootVm: RootNavViewModel = hiltViewModel()
    val destination by rootVm.destination.collectAsStateWithLifecycle()

    KsetraTheme {
        when (destination) {
            RootDestination.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            RootDestination.Setup -> {
                SetupScreen(onFinished = { rootVm.markSetupFinished() })
            }
            RootDestination.BootDiagnostics -> {
                BootDiagnosticsScreen(
                    onProceed = { rootVm.markBootDiagnosticsDone() },
                    onNavigateToConnectivity = { rootVm.openConnectivityFromBoot() }
                )
            }
            RootDestination.ConnectivityFromBoot -> {
                ConnectivityCheckScreen(
                    onNavigateBack = { rootVm.backFromBootConnectivity() }
                )
            }
            RootDestination.Main -> {
                MainNavHost()
            }
        }
    }
}

@Composable
private fun MainNavHost(navController: NavHostController = rememberNavController()) {
    MainSmsPermissionEffect()
    NavHost(
        navController = navController,
        startDestination = Screen.Hub.route
    ) {
        composable(Screen.Hub.route) {
            HubScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route)
                },
                onNavigateToSuraksha = {
                    navController.navigate(Screen.SurakshaDashboard.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToBackup = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToDataDiagnostics = {
                    navController.navigate(Screen.DataDiagnostics.route)
                },
                onNavigateToConnectivity = {
                    navController.navigate(Screen.ConnectivityCheck.route)
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
        ) { backStackEntry ->
            val initialQuery = backStackEntry.arguments?.getString("initialQuery")
            ChatScreen(
                initialQuery = initialQuery,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDataDiagnostics = {
                    navController.navigate(Screen.DataDiagnostics.route)
                },
                onNavigateToConnectivity = {
                    navController.navigate(Screen.ConnectivityCheck.route)
                }
            )
        }

        composable(Screen.ConnectivityCheck.route) {
            ConnectivityCheckScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DataDiagnostics.route) {
            DataDiagnosticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SurakshaDashboard.route) {
            SurakshaDashboardScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCameraMatrix = {
                    navController.navigate(Screen.CameraMatrix.route)
                }
            )
        }

        composable(Screen.CameraMatrix.route) {
            CameraMatrixScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
