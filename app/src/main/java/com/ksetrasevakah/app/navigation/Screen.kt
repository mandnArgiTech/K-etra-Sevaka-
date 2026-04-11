package com.ksetrasevakah.app.navigation

sealed class Screen(val route: String) {
    data object Hub : Screen("hub")
    data object Dashboard : Screen("dashboard")
    data object Chat : Screen("chat/{initialQuery}") {
        fun createRoute(initialQuery: String? = null) = "chat/${initialQuery ?: ""}"
    }
    data object Settings : Screen("settings")
    data object DataDiagnostics : Screen("data_diagnostics")
    data object ConnectivityCheck : Screen("connectivity_check")
    data object SurakshaDashboard : Screen("suraksha_dashboard")
    data object CameraMatrix : Screen("camera_matrix")
}
