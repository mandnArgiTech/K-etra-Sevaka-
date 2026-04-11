package com.ksetrasevakah.app.navigation

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

private val smsPermissions = listOf(
    Manifest.permission.READ_SMS,
    Manifest.permission.RECEIVE_SMS,
    Manifest.permission.SEND_SMS
)

/**
 * Requests SMS permissions when the main app graph is shown (each cold start if not already granted).
 * Navigation and other modules remain usable if the user denies.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainSmsPermissionEffect() {
    val state = rememberMultiplePermissionsState(smsPermissions)
    LaunchedEffect(Unit) {
        if (!state.allPermissionsGranted) {
            state.launchMultiplePermissionRequest()
        }
    }
}
