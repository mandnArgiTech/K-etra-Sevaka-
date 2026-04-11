package com.ksetrasevakah.feature.connectivity

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ksetrasevakah.core.sms.SmsHistoryReader
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraSurface
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SMS + notification listener diagnostics. Used from [ConnectivityCheckScreen] and first-run
 * [SetupScreen] so fundamentals are verified before large model downloads.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ConnectivityDiagnosticsBody(
    viewModel: ConnectivityCheckViewModel,
    smsPermissions: MultiplePermissionsState,
    modifier: Modifier = Modifier,
    embeddedHeaderTitle: String? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        if (embeddedHeaderTitle != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KsetraSpacing.screenPadding, vertical = KsetraSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = embeddedHeaderTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = KsetraTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = KsetraAccentGreen
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(KsetraSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(KsetraSpacing.lg)
        ) {
            SectionHeader(text = "SMS ACCESS")
            KsetraCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                ) {
                    StatusRow(
                        label = "READ_SMS permission",
                        granted = state.sms.hasPermission
                    )

                    if (!state.sms.hasPermission) {
                        Spacer(modifier = Modifier.height(KsetraSpacing.xs))
                        Button(
                            onClick = { smsPermissions.launchMultiplePermissionRequest() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = KsetraAccentGreen)
                        ) {
                            Text("Grant SMS Permission")
                        }
                    }

                    Spacer(modifier = Modifier.height(KsetraSpacing.xs))

                    LabelValue("Configured panel number", state.sms.panelNumber)

                    if (state.sms.hasPermission) {
                        if (state.sms.panelMessageCount > 0) {
                            LabelValue(
                                "Messages matched",
                                "${state.sms.panelMessageCount} SMS"
                            )
                            state.sms.oldestPanelMessageMs?.let { oldest ->
                                LabelValue("Oldest", formatDate(oldest))
                            }
                            state.sms.newestPanelMessageMs?.let { newest ->
                                LabelValue("Newest", formatDate(newest))
                            }
                        } else if (!state.sms.isScanning) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA000),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "No messages found from ${state.sms.panelNumber}. " +
                                        "Scan inbox to pick the correct number.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFFFA000)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(KsetraSpacing.sm))

                        if (state.sms.isScanning) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = KsetraAccentGreen
                            )
                            Text(
                                "Scanning inbox…",
                                style = MaterialTheme.typography.bodySmall,
                                color = KsetraTextSecondary
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.scanInbox() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Scan inbox", color = KsetraAccentGreen)
                                }
                                Button(
                                    onClick = { viewModel.showNumberPicker() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = KsetraAccentGreen)
                                ) {
                                    Text("Change number")
                                }
                            }
                        }

                        state.sms.scanError?.let { err ->
                            Text(
                                text = "Error: $err",
                                style = MaterialTheme.typography.bodySmall,
                                color = KsetraRed
                            )
                        }
                    }
                }
            }

            SectionHeader(text = "NOTIFICATION LISTENER (SURAKSHA)")
            KsetraCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                ) {
                    StatusRow(
                        label = "Notification listener access",
                        granted = state.notif.listenerGranted
                    )

                    if (!state.notif.listenerGranted) {
                        Text(
                            text = "The notification listener is not enabled. " +
                                "Tap below to open Android settings and enable it for this app.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KsetraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(KsetraSpacing.xs))
                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = KsetraAccentGreen)
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(KsetraSpacing.sm))
                            Text("Open notification settings")
                        }
                    } else {
                        Text(
                            text = "Listener is active. Any Tapo / camera app notification " +
                                "will be processed by Suraksha.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KsetraTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(KsetraSpacing.xs))

                    LabelValue("Monitored app package", state.notif.watchedPackage)
                    if (state.notif.watchedAppLabel.isNotEmpty() &&
                        state.notif.watchedAppLabel != state.notif.watchedPackage
                    ) {
                        LabelValue("App name", state.notif.watchedAppLabel)
                    }

                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))

                    OutlinedButton(
                        onClick = { viewModel.showAppPicker() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Change monitored app", color = KsetraAccentGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))
        }
    }

    if (state.sms.showNumberPicker) {
        SmsNumberPickerDialog(
            senders = state.sms.allSenders,
            isLoading = state.sms.isScanning,
            currentNumber = state.sms.panelNumber,
            onSelect = { viewModel.selectPanelNumber(it) },
            onDismiss = { viewModel.dismissNumberPicker() }
        )
    }

    if (state.notif.showAppPicker) {
        AppPickerDialog(
            apps = state.notif.installedApps,
            isLoading = state.notif.isLoadingApps,
            currentPackage = state.notif.watchedPackage,
            onSelect = { viewModel.selectWatchedApp(it) },
            onDismiss = { viewModel.dismissAppPicker() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ConnectivityCheckScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConnectivityCheckViewModel = hiltViewModel()
) {
    val smsPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
        )
    )

    LaunchedEffect(smsPermissions.allPermissionsGranted) {
        if (smsPermissions.allPermissionsGranted) viewModel.refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
    ) {
        TopAppBar(
            title = { Text("Connectivity Check", color = KsetraTextPrimary) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = KsetraTextPrimary
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = KsetraAccentGreen)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = KsetraSurface)
        )

        ConnectivityDiagnosticsBody(
            viewModel = viewModel,
            smsPermissions = smsPermissions,
            modifier = Modifier.weight(1f),
            embeddedHeaderTitle = null
        )
    }
}

@Composable
private fun SmsNumberPickerDialog(
    senders: List<SmsHistoryReader.SenderInfo>,
    isLoading: Boolean,
    currentNumber: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KsetraSurface,
        title = {
            Text(
                "Select panel SMS number",
                color = KsetraTextPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = KsetraAccentGreen)
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    Text("Scanning inbox…", color = KsetraTextSecondary)
                }
            } else if (senders.isEmpty()) {
                Text(
                    "No SMS found in inbox. Make sure SMS permission is granted.",
                    color = KsetraTextSecondary
                )
            } else {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(senders) { sender ->
                        val isSelected = sender.normalizedAddress == currentNumber ||
                            sender.address == currentNumber
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(sender.normalizedAddress) }
                                .background(
                                    if (isSelected) KsetraAccentGreen.copy(alpha = 0.15f)
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = KsetraSpacing.sm, horizontal = KsetraSpacing.xs)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = KsetraAccentGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = sender.address,
                                    color = if (isSelected) KsetraAccentGreen else KsetraTextPrimary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                            Text(
                                text = "${sender.messageCount} messages · " +
                                    "${fmt.format(Date(sender.oldestMessageMs))} – " +
                                    fmt.format(Date(sender.newestMessageMs)),
                                style = MaterialTheme.typography.bodySmall,
                                color = KsetraTextSecondary
                            )
                            Text(
                                text = sender.snippet,
                                style = MaterialTheme.typography.bodySmall,
                                color = KsetraTextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KsetraTextSecondary)
            }
        }
    )
}

@Composable
private fun AppPickerDialog(
    apps: List<AppInfo>,
    isLoading: Boolean,
    currentPackage: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KsetraSurface,
        title = {
            Text(
                "Select app to monitor",
                color = KsetraTextPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = KsetraAccentGreen)
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    Text("Loading apps…", color = KsetraTextSecondary)
                }
            } else {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(apps) { app ->
                        val isSelected = app.packageName == currentPackage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(app.packageName) }
                                .background(
                                    if (isSelected) KsetraAccentGreen.copy(alpha = 0.15f)
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = KsetraSpacing.sm, horizontal = KsetraSpacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = KsetraAccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = app.label,
                                    color = if (isSelected) KsetraAccentGreen else KsetraTextPrimary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KsetraTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KsetraTextSecondary)
            }
        }
    )
}

@Composable
private fun StatusRow(label: String, granted: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
    ) {
        Icon(
            imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (granted) KsetraAccentGreen else KsetraRed,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = KsetraTextPrimary
        )
        Text(
            text = if (granted) "GRANTED" else "DENIED",
            style = MaterialTheme.typography.labelMedium,
            color = if (granted) KsetraAccentGreen else KsetraRed,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = KsetraTextSecondary,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = KsetraTextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.55f)
        )
    }
}

private fun formatDate(ms: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(ms))
