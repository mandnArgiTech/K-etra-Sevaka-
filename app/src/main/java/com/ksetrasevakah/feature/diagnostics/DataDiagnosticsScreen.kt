package com.ksetrasevakah.feature.diagnostics

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraSurface
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.database.entity.VectorDocumentEntity
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DataDiagnosticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DataDiagnosticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val smsPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
        )
    )
    val dateFmt = remember {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
    }
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Data & ingestion",
                    color = KsetraTextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            },
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
                IconButton(
                    onClick = { viewModel.refresh() },
                    enabled = !uiState.isLoading && !uiState.reingest.isRunning
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = KsetraAccentGreen
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = KsetraSurface)
        )

        if (uiState.isLoading && uiState.overview == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(KsetraSpacing.xxl),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = KsetraAccentGreen)
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = KsetraSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(KsetraSpacing.lg)
        ) {
            item { Spacer(modifier = Modifier.height(KsetraSpacing.sm)) }

            item(key = "reingest") {
                SectionHeader(text = "RE-INGEST FROM INBOX")
                Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                KsetraCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(KsetraSpacing.md)) {
                        Text(
                            text = "Runs every matching panel SMS through the same pipeline as new messages " +
                                "(telemetry row + RAG chunk). Use after granting SMS permission or fixing the panel number.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KsetraTextSecondary
                        )
                        if (!smsPermissions.allPermissionsGranted) {
                            OutlinedButton(
                                onClick = { smsPermissions.launchMultiplePermissionRequest() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Request SMS permission", color = KsetraTextPrimary)
                            }
                        }
                        Button(
                            onClick = { viewModel.startReingestFromInbox(500) },
                            enabled = !uiState.reingest.isRunning && !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = scheme.primary,
                                contentColor = scheme.onPrimary
                            )
                        ) {
                            Text("Re-ingest last 500 panel messages")
                        }
                        if (uiState.reingest.isRunning) {
                            if (uiState.reingest.total > 0) {
                                LinearProgressIndicator(
                                    progress = {
                                        uiState.reingest.done.toFloat() /
                                            uiState.reingest.total.coerceAtLeast(1)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = scheme.primary,
                                    trackColor = scheme.surfaceVariant,
                                    drawStopIndicator = {}
                                )
                            } else {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = scheme.primary,
                                    trackColor = scheme.surfaceVariant
                                )
                            }
                            uiState.reingest.progressLabel?.let { label ->
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = KsetraTextSecondary
                                )
                            }
                        }
                        uiState.reingest.finishedMessage?.let { msg ->
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = KsetraAccentGreen
                            )
                        }
                        uiState.reingest.error?.let { msg ->
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            val errText = uiState.error
            if (errText != null) {
                item(key = "error") {
                    Text(
                        text = errText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = KsetraSpacing.sm)
                    )
                }
            }

            val overview = uiState.overview
            if (overview != null) {
                item(key = "ingestion_header") {
                    SectionHeader(text = "INGESTION & RAG")
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    KsetraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)) {
                            Text(
                                text = "Panel SMS stored as telemetry rows. Each row can produce one vector " +
                                    "chunk for chat (RAG). Counts below are live from SQLite.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = KsetraTextSecondary
                            )
                            Spacer(modifier = Modifier.height(KsetraSpacing.xs))
                            StatRow("Telemetry rows (SMS-derived)", overview.telemetryRowCount.toString())
                            StatRow(
                                "Newest row (panel message time)",
                                overview.telemetryNewestMs.formatDate(dateFmt)
                            )
                            StatRow(
                                "Oldest row (panel message time)",
                                overview.telemetryOldestMs.formatDate(dateFmt)
                            )
                            StatRow(
                                "Last pipeline write (this device)",
                                overview.lastPipelineIngestWallClockMs.formatDate(dateFmt)
                            )
                            StatRow("Vector chunks (RAG index)", overview.vectorChunkCount.toString())
                            StatRow(
                                "Newest chunk stored at",
                                overview.vectorNewestMs.formatDate(dateFmt)
                            )
                            StatRow(
                                "Oldest chunk stored at",
                                overview.vectorOldestMs.formatDate(dateFmt)
                            )
                            if (overview.telemetryRowCount == 0L) {
                                Text(
                                    text = "No telemetry yet — grant SMS permission, complete setup, " +
                                        "and ensure the panel sends messages to this phone.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = KsetraAccentGreen,
                                    modifier = Modifier.padding(top = KsetraSpacing.sm)
                                )
                            }
                        }
                    }
                }

                item(key = "other_tables") {
                    SectionHeader(text = "OTHER TABLES (COUNTS)")
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    KsetraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)) {
                            StatRow("worker_activity", overview.workerActivityRowCount.toString())
                            StatRow("fault_log", overview.faultRowCount.toString())
                            StatRow("chat_threads", overview.chatThreadCount.toString())
                            StatRow("chat_messages", overview.chatMessageCount.toString())
                            StatRow("security_events", overview.securityEventCount.toString())
                            StatRow("backup_log", overview.backupLogRowCount.toString())
                            Spacer(modifier = Modifier.height(KsetraSpacing.xs))
                            StatRow("First-run setup finished", overview.firstRunComplete.toString())
                            StatRow("Models downloaded flag", overview.modelsDownloaded.toString())
                        }
                    }
                }
            }

            item {
                SectionHeader(text = "RECENT TELEMETRY (RAW SMS SNIPPET)")
                Spacer(modifier = Modifier.height(KsetraSpacing.sm))
            }

            if (uiState.recentTelemetry.isEmpty()) {
                item {
                    Text(
                        text = "No rows in telemetry_log.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraTextSecondary,
                        modifier = Modifier.padding(bottom = KsetraSpacing.md)
                    )
                }
            } else {
                items(uiState.recentTelemetry, key = { it.id }) { row ->
                    TelemetryPreviewCard(row, dateFmt)
                }
            }

            item {
                SectionHeader(text = "RECENT VECTOR CHUNKS")
                Spacer(modifier = Modifier.height(KsetraSpacing.sm))
            }

            if (uiState.recentVectorChunks.isEmpty()) {
                item {
                    Text(
                        text = "No rows in vector_documents.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraTextSecondary,
                        modifier = Modifier.padding(bottom = KsetraSpacing.xxl)
                    )
                }
            } else {
                items(uiState.recentVectorChunks, key = { it.id }) { doc ->
                    VectorChunkCard(doc, dateFmt)
                }
            }

            item { Spacer(modifier = Modifier.height(KsetraSpacing.xxxl)) }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = KsetraTextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = KsetraTextPrimary,
            modifier = Modifier.padding(start = KsetraSpacing.md)
        )
    }
}

@Composable
private fun TelemetryPreviewCard(
    row: TelemetryEntity,
    dateFmt: DateFormat
) {
    KsetraCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = KsetraSpacing.sm)
    ) {
        Column {
            Text(
                text = dateFmt.format(Date(row.timestamp)) + " · motorOn=${row.motorOn}",
                style = MaterialTheme.typography.labelLarge,
                color = KsetraAccentGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = row.rawSms.take(220) + if (row.rawSms.length > 220) "…" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = KsetraTextPrimary,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VectorChunkCard(
    doc: VectorDocumentEntity,
    dateFmt: DateFormat
) {
    KsetraCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = KsetraSpacing.sm)
    ) {
        Column {
            Text(
                text = dateFmt.format(Date(doc.createdAt)) + " · id=${doc.id}",
                style = MaterialTheme.typography.labelLarge,
                color = KsetraAccentGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = doc.text.take(200) + if (doc.text.length > 200) "…" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = KsetraTextPrimary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "meta: " + doc.metadataJson.take(120) +
                    if (doc.metadataJson.length > 120) "…" else "",
                style = MaterialTheme.typography.bodySmall,
                color = KsetraTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun Long?.formatDate(fmt: DateFormat): String =
    when (this) {
        null -> "—"
        else -> fmt.format(Date(this))
    }
