package com.ksetrasevakah.feature.settings

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                }
            }

            SectionHeader(text = "AI MODELS")
            KsetraCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsRow("Ingestion Model", uiState.ingestionModelId)
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    SettingsRow("Orchestrator Model", uiState.orchestratorModelId)
                }
            }

            SectionHeader(text = "BACKUP")
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
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    Button(
                        onClick = { viewModel.triggerBackup() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KsetraAccentGreenDim,
                            contentColor = KsetraAccentGreen
                        ),
                        enabled = uiState.backupStatus !is BackupStatus.InProgress
                    ) {
                        Text("Backup Now")
                    }
                }
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
