package com.ksetrasevakah.feature.pumpiq.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ksetrasevakah.designsystem.component.GlowEffect
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.designsystem.theme.KsetraAmber
import com.ksetrasevakah.designsystem.theme.KsetraBlue
import com.ksetrasevakah.designsystem.theme.KsetraPurple
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.feature.pumpiq.dashboard.component.ChartTabBar
import com.ksetrasevakah.feature.pumpiq.dashboard.component.FaultDistributionChart
import com.ksetrasevakah.feature.pumpiq.dashboard.component.FaultSlice
import com.ksetrasevakah.feature.pumpiq.dashboard.component.GridReliabilityChart
import com.ksetrasevakah.feature.pumpiq.dashboard.component.GridReliabilityEntry
import com.ksetrasevakah.feature.pumpiq.dashboard.component.MotorControlCard
import com.ksetrasevakah.feature.pumpiq.dashboard.component.PhaseCurrentChart
import com.ksetrasevakah.feature.pumpiq.dashboard.component.PowerForecastChart
import com.ksetrasevakah.feature.pumpiq.dashboard.component.PowerForecastPoint
import com.ksetrasevakah.feature.pumpiq.dashboard.component.PredictionCardsSection
import com.ksetrasevakah.feature.pumpiq.dashboard.model.ChartTab
import com.ksetrasevakah.feature.pumpiq.dashboard.model.DashboardUiEvent
import com.ksetrasevakah.feature.pumpiq.dashboard.model.DashboardUiState

@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String?) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.commandFeedback, uiState.error) {
        val cmd = uiState.commandFeedback
        val err = uiState.error
        val text = when {
            !cmd.isNullOrBlank() -> cmd.trim()
            !err.isNullOrBlank() -> err.trim()
            cmd != null || err != null -> {
                viewModel.onEvent(DashboardUiEvent.ErrorConsumed)
                return@LaunchedEffect
            }
            else -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(text)
        viewModel.onEvent(DashboardUiEvent.ErrorConsumed)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { target ->
            when (target) {
                is DashboardViewModel.NavigationTarget.Chat -> onNavigateToChat(target.query)
                is DashboardViewModel.NavigationTarget.Back -> onNavigateBack()
            }
        }
    }

    DashboardContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    onEvent: (DashboardUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
    ) {
        GlowEffect(
            color = KsetraAccentGreen,
            size = 250.dp,
            offsetX = (-40).dp,
            offsetY = (-30).dp,
            opacity = 0.05f
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = KsetraAccentGreen
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(KsetraSpacing.screenPadding)
            ) {
                DashboardHeader(
                    onBack = { onEvent(DashboardUiEvent.NavigateBack) },
                    onChat = { onEvent(DashboardUiEvent.NavigateToChat()) }
                )

                Spacer(modifier = Modifier.height(KsetraSpacing.lg))

                MotorControlCard(
                    motorState = state.motorState,
                    phaseR = state.phaseR,
                    phaseY = state.phaseY,
                    phaseB = state.phaseB,
                    sessionStartTime = state.sessionStartTime,
                    onStart = { onEvent(DashboardUiEvent.StartPump) },
                    onStop = { onEvent(DashboardUiEvent.StopPump) }
                )

                Spacer(modifier = Modifier.height(KsetraSpacing.xxl))

                PredictionCardsSection(state = state.predictions)

                Spacer(modifier = Modifier.height(KsetraSpacing.xxl))

                if (state.dailySummary.isNotEmpty()) {
                    SectionHeader(text = "AI Summary")
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    AiSummaryCard(summary = state.dailySummary)
                    Spacer(modifier = Modifier.height(KsetraSpacing.xxl))
                }

                SectionHeader(text = "Analytics")

                Spacer(modifier = Modifier.height(KsetraSpacing.md))

                ChartTabBar(
                    activeTab = state.activeChart,
                    onTabSelected = { onEvent(DashboardUiEvent.SelectChart(it)) }
                )

                Spacer(modifier = Modifier.height(KsetraSpacing.lg))

                ChartArea(state = state)

                Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun DashboardHeader(
    onBack: () -> Unit,
    onChat: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = KsetraTextPrimary
            )
        }

        Text(
            text = "PumpIQ",
            style = MaterialTheme.typography.headlineMedium,
            color = KsetraTextPrimary
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(KsetraAccentGreen.copy(alpha = 0.1f))
                .clickable(onClick = onChat),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Chat",
                tint = KsetraAccentGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AiSummaryCard(summary: String) {
    KsetraCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Daily Intelligence",
                style = MaterialTheme.typography.labelLarge,
                color = KsetraAccentGreen
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.sm))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = KsetraTextPrimary
            )
        }
    }
}

@Composable
private fun ChartArea(state: DashboardUiState) {
    val faultColors = listOf(KsetraRed, KsetraAmber, KsetraBlue, KsetraPurple, KsetraAccentGreen)
    val gridEntries = state.gridReliabilityRows.map {
        GridReliabilityEntry(it.label, it.uptimeHours, it.downtimeHours)
    }
    val faultSlices = state.faultDistribution.mapIndexed { index, pair ->
        FaultSlice(
            label = pair.first,
            value = pair.second,
            color = faultColors[index % faultColors.size]
        )
    }
    val n = state.powerVoltageHistory.size
    val actualPower = if (n > 0) {
        state.powerVoltageHistory.mapIndexed { i, v ->
            val offset = if (n > 1) i * 24f / (n - 1) else 0f
            PowerForecastPoint(hourOffset = offset, voltage = v)
        }
    } else {
        emptyList()
    }
    val vPred = state.voltage ?: 220f
    val predictedPower = buildList {
        state.predictions.powerFailureTime?.let { time ->
            val hour = time.substringBefore(':').toIntOrNull()?.toFloat()
            if (hour != null) {
                add(PowerForecastPoint(hourOffset = hour, voltage = vPred))
            }
        }
    }

    KsetraCard(modifier = Modifier.fillMaxWidth()) {
        when (state.activeChart) {
            ChartTab.PHASE -> {
                PhaseCurrentChart(
                    phaseRData = state.phaseSeriesR,
                    phaseYData = state.phaseSeriesY,
                    phaseBData = state.phaseSeriesB
                )
            }
            ChartTab.UPTIME -> {
                GridReliabilityChart(entries = gridEntries)
            }
            ChartTab.FAULTS -> {
                FaultDistributionChart(slices = faultSlices)
            }
            ChartTab.WORKER -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(KsetraSpacing.lg)
                ) {
                    Text(
                        text = "Expected worker start",
                        style = MaterialTheme.typography.labelLarge,
                        color = KsetraAccentGreen
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    Text(
                        text = state.predictions.workerOnTime ?: "—",
                        style = MaterialTheme.typography.headlineSmall,
                        color = KsetraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                    Text(
                        text = if (state.predictions.hasInsufficientData) {
                            "Collect more worker activity to refine this estimate."
                        } else {
                            "Based on recent on-site patterns."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = KsetraTextSecondary
                    )
                }
            }
            ChartTab.POWER -> {
                if (actualPower.isEmpty() && predictedPower.isEmpty()) {
                    Text(
                        text = "Voltage history will appear as telemetry arrives.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KsetraTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(KsetraSpacing.lg)
                    )
                } else {
                    PowerForecastChart(
                        actual = actualPower,
                        predicted = predictedPower
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    KsetraTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        DashboardContent(
            state = DashboardUiState(
                isLoading = false,
                motorState = MotorState.ON,
                phaseR = 3.82f,
                phaseY = 3.91f,
                phaseB = 3.78f,
                voltage = 228f,
                temperature = 42f,
                sessionStartTime = System.currentTimeMillis() - 3_600_000,
                dailySummary = "Motor ran for 6h today. Phase balance stable. Grid had 2 brief outages totaling 45 min."
            ),
            onEvent = {},
            snackbarHostState = snackbarHostState
        )
    }
}
