package com.ksetrasevakah.feature.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import com.ksetrasevakah.core.backup.model.BackupStatus
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreenDim
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraPurple
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraSurface
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDataDiagnostics: () -> Unit = {},
    onNavigateToConnectivity: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPanelNumberDialog by rememberSaveable { mutableStateOf(false) }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onGoogleSignInResult(result.data)
    }

    LaunchedEffect(uiState.restoreMessage) {
        if (!uiState.restoreMessage.isNullOrBlank()) {
            delay(4_000)
            viewModel.clearRestoreMessage()
        }
    }

    val backupDateFormat = remember {
        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
    ) {
        TopAppBar(
            title = { Text("Settings", color = KsetraTextPrimary) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = KsetraTextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = KsetraSurface
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(KsetraSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(KsetraSpacing.lg)
        ) {
            SectionHeader(text = "PANEL INFO")
            KsetraCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsRow("Panel Number", uiState.panelNumber)
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    SettingsRow("Motor State", uiState.motorState.displayLabel)
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                    ) {
                        OutlinedButton(
                            onClick = { showPanelNumberDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Edit number", color = KsetraAccentGreen)
                        }
                        OutlinedButton(
                            onClick = onNavigateToConnectivity,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test SMS", color = KsetraAccentGreen)
                        }
                    }
                }
            }

            SectionHeader(text = "AI MODELS")
            KsetraCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsRow("Chat Model (GPU)", uiState.chatModelId)
                    Text(
                        text = uiState.orchestratorModelDetails,
                        style = MaterialTheme.typography.bodySmall,
                        color = KsetraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    SettingsRow("Embeddings (MiniLM)", uiState.embeddingModelDetails)
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    OutlinedButton(
                        onClick = { viewModel.refreshModelStatus() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Refresh model status", color = KsetraAccentGreen)
                    }
                }
            }

            SectionHeader(text = "BACKUP & GOOGLE DRIVE")
            KsetraCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val statusText = when (uiState.backupStatus) {
                        is BackupStatus.Idle -> "No recent backup"
                        is BackupStatus.InProgress -> "Backup in progress..."
                        is BackupStatus.Success -> "Last backup successful"
                        is BackupStatus.Error -> "Backup failed: ${(uiState.backupStatus as BackupStatus.Error).message}"
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = KsetraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    Text(
                        text = uiState.driveAccountEmail?.let { "Signed in as $it" }
                            ?: "Not signed in — uploads only work after Google sign-in.",
                        style = MaterialTheme.typography.bodySmall,
                        color = KsetraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { signInLauncher.launch(viewModel.googleSignInIntent()) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KsetraAccentGreenDim,
                                contentColor = KsetraTextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sign in")
                        }
                        OutlinedButton(
                            onClick = { viewModel.signOutGoogle() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sign out", color = KsetraTextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    Button(
                        onClick = { viewModel.triggerBackup() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KsetraAccentGreenDim,
                            contentColor = KsetraTextPrimary
                        ),
                        enabled = uiState.backupStatus !is BackupStatus.InProgress,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Backup Now")
                    }
                    if (!uiState.restoreMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                        Text(
                            text = uiState.restoreMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = KsetraAccentGreen
                        )
                    }
                    if (uiState.localBackupNames.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(KsetraSpacing.md))
                        Text(
                            text = "Local backups",
                            style = MaterialTheme.typography.labelLarge,
                            color = KsetraTextPrimary
                        )
                        uiState.localBackupNames.forEach { name ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KsetraTextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { viewModel.restoreFromLocalBackup(name) }) {
                                    Text("Restore", color = KsetraPurple)
                                }
                            }
                        }
                    }
                    if (uiState.driveBackups.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(KsetraSpacing.md))
                        Text(
                            text = "Drive backups",
                            style = MaterialTheme.typography.labelLarge,
                            color = KsetraTextPrimary
                        )
                        uiState.driveBackups.forEach { info ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = info.fileName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KsetraTextSecondary
                                    )
                                    Text(
                                        text = backupDateFormat.format(Date(info.createdAt)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = KsetraTextSecondary
                                    )
                                }
                                TextButton(onClick = { viewModel.restoreFromDrive(info.fileId) }) {
                                    Text("Restore", color = KsetraPurple)
                                }
                            }
                        }
                    }
                }
            }

            SectionHeader(text = "DATA")
            KsetraCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "See how many SMS rows and RAG chunks are stored, browse recent SQLite rows, " +
                            "and re-ingest inbox history from the same screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KsetraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    OutlinedButton(
                        onClick = onNavigateToDataDiagnostics,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open data & ingestion", color = KsetraTextPrimary)
                    }
                }
            }

            SectionHeader(text = "ABOUT")
            KsetraCard {
                SettingsRow("Version", uiState.appVersion)
            }

            SectionHeader(text = "WATCHDOG")
            KsetraCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Forgot-Off Detection",
                            style = MaterialTheme.typography.bodyLarge,
                            color = KsetraTextPrimary
                        )
                        Text(
                            text = "Alert if motor runs past schedule",
                            style = MaterialTheme.typography.bodySmall,
                            color = KsetraTextSecondary
                        )
                    }
                    Switch(
                        checked = uiState.watchdogEnabled,
                        onCheckedChange = { viewModel.toggleWatchdog(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = KsetraAccentGreen,
                            checkedTrackColor = KsetraAccentGreenDim,
                            uncheckedThumbColor = KsetraTextSecondary,
                            uncheckedTrackColor = KsetraSurface
                        )
                    )
                }
            }
        }
    }

    if (showPanelNumberDialog) {
        PanelNumberEditDialog(
            current = uiState.panelNumber,
            onConfirm = { newNumber ->
                viewModel.setPanelNumber(newNumber)
                showPanelNumberDialog = false
            },
            onDismiss = { showPanelNumberDialog = false }
        )
    }
}

@Composable
private fun PanelNumberEditDialog(
    current: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by rememberSaveable { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KsetraSurface,
        title = {
            Text("Edit panel number", color = KsetraTextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)) {
                Text(
                    text = "Enter the phone number (digits only) from which panel SMS are sent.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KsetraTextSecondary
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Panel number", color = KsetraTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KsetraAccentGreen,
                        unfocusedBorderColor = KsetraTextSecondary,
                        focusedTextColor = KsetraTextPrimary,
                        unfocusedTextColor = KsetraTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (value.isNotBlank()) onConfirm(value.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = KsetraAccentGreen)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KsetraTextSecondary)
            }
        }
    )
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = KsetraTextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = KsetraTextPrimary
        )
    }
}
