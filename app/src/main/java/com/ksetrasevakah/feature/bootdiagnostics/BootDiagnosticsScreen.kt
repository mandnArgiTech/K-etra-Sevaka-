package com.ksetrasevakah.feature.bootdiagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAmber
import com.ksetrasevakah.designsystem.theme.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import kotlinx.coroutines.delay

@Composable
fun BootDiagnosticsScreen(
    onProceed: () -> Unit,
    onNavigateToConnectivity: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BootDiagnosticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var secondsLeft by remember { mutableIntStateOf(4) }
    var countdownStopped by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.fastChecksRunning) {
        if (uiState.fastChecksRunning) return@LaunchedEffect
        while (secondsLeft > 0 && !countdownStopped) {
            delay(1_000)
            if (countdownStopped) return@LaunchedEffect
            if (viewModel.uiState.value.benchmarkRunning) continue
            secondsLeft--
        }
        if (!countdownStopped && !viewModel.uiState.value.benchmarkRunning) {
            onProceed()
        }
    }

    val hasIssues = uiState.checks.any {
        it.status == CheckStatus.Warn || it.status == CheckStatus.Fail
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Boot diagnostics",
            style = MaterialTheme.typography.headlineMedium,
            color = KsetraTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Quick health checks before the hub.",
            style = MaterialTheme.typography.bodyMedium,
            color = KsetraTextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.fastChecksRunning) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = KsetraAccentGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(uiState.checks, key = { it.id }) { check ->
                if (check.id == BootDiagnosticsViewModel.ID_BENCHMARK) {
                    BenchmarkDiagnosticCard(
                        check = check,
                        benchmarkRunning = uiState.benchmarkRunning,
                        onRunBenchmark = { viewModel.runOrchestratorBenchmark() }
                    )
                } else {
                    DiagnosticCard(check = check)
                }
            }
        }

        uiState.benchmarkError?.let { err ->
            Text(
                text = err,
                color = KsetraRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    countdownStopped -> "Paused"
                    secondsLeft > 0 -> "Continuing in ${secondsLeft}s…"
                    else -> "Proceeding…"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = KsetraTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    countdownStopped = true
                    onProceed()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KsetraAccentGreen,
                    contentColor = Color(0xFF030A06)
                )
            ) {
                Text("Continue now")
            }
            if (hasIssues) {
                OutlinedButton(
                    onClick = {
                        countdownStopped = true
                        onNavigateToConnectivity()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KsetraAmber)
                ) {
                    Text("Fix issues")
                }
            }
        }
    }
}

@Composable
private fun DiagnosticCard(check: DiagnosticCheck) {
    val (icon, tint) = when (check.status) {
        CheckStatus.Running -> Icons.Default.HourglassEmpty to KsetraTextSecondary
        CheckStatus.Pass -> Icons.Default.CheckCircle to KsetraAccentGreen
        CheckStatus.Warn -> Icons.Default.Warning to KsetraAmber
        CheckStatus.Fail -> Icons.Default.Close to KsetraRed
        CheckStatus.Idle -> Icons.Default.HourglassEmpty to KsetraTextSecondary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(KsetraCard, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = check.label,
                style = MaterialTheme.typography.titleSmall,
                color = KsetraTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = check.detail,
            style = MaterialTheme.typography.bodySmall,
            color = KsetraTextSecondary
        )
    }
}

@Composable
private fun BenchmarkDiagnosticCard(
    check: DiagnosticCheck,
    benchmarkRunning: Boolean,
    onRunBenchmark: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(KsetraCard, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when {
                benchmarkRunning || check.status == CheckStatus.Running -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = KsetraAccentGreen
                    )
                }
                check.status == CheckStatus.Pass -> androidx.compose.material3.Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = KsetraAccentGreen,
                    modifier = Modifier.size(22.dp)
                )
                check.status == CheckStatus.Warn -> androidx.compose.material3.Icon(
                    Icons.Default.Warning,
                    null,
                    tint = KsetraAmber,
                    modifier = Modifier.size(22.dp)
                )
                check.status == CheckStatus.Fail -> androidx.compose.material3.Icon(
                    Icons.Default.Close,
                    null,
                    tint = KsetraRed,
                    modifier = Modifier.size(22.dp)
                )
                else -> androidx.compose.material3.Icon(
                    Icons.Default.HourglassEmpty,
                    null,
                    tint = KsetraTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = check.label,
                style = MaterialTheme.typography.titleSmall,
                color = KsetraTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = check.detail,
            style = MaterialTheme.typography.bodySmall,
            color = KsetraTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!benchmarkRunning && check.status == CheckStatus.Idle) {
            OutlinedButton(
                onClick = onRunBenchmark,
                enabled = !benchmarkRunning,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KsetraAccentGreen)
            ) {
                Text("Run benchmark")
            }
        }
    }
}
