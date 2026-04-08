package com.ksetrasevakah.feature.suraksha.dashboard

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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ksetrasevakah.designsystem.component.GlowEffect
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue
import com.ksetrasevakah.feature.suraksha.dashboard.component.SecurityBriefingCard
import com.ksetrasevakah.feature.suraksha.dashboard.component.ThreatHeatmap
import com.ksetrasevakah.feature.suraksha.dashboard.component.ThreatLedger
import com.ksetrasevakah.feature.suraksha.dashboard.component.ThreatSummaryRow
import com.ksetrasevakah.feature.suraksha.dashboard.model.SurakshaDashboardUiEvent
import com.ksetrasevakah.feature.suraksha.dashboard.model.SurakshaDashboardUiState

@Composable
fun SurakshaDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCameraMatrix: () -> Unit,
    viewModel: SurakshaDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { target ->
            when (target) {
                is SurakshaDashboardViewModel.NavigationTarget.Back -> onNavigateBack()
                is SurakshaDashboardViewModel.NavigationTarget.CameraMatrix -> onNavigateToCameraMatrix()
            }
        }
    }

    SurakshaDashboardContent(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun SurakshaDashboardContent(
    state: SurakshaDashboardUiState,
    onEvent: (SurakshaDashboardUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
    ) {
        GlowEffect(
            color = SurakshaShieldBlue,
            size = 250.dp,
            offsetX = (-40).dp,
            offsetY = (-30).dp,
            opacity = 0.05f
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = SurakshaShieldBlue
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(KsetraSpacing.screenPadding)
            ) {
                SurakshaDashboardHeader(
                    onBack = { onEvent(SurakshaDashboardUiEvent.NavigateBack) },
                    onCameraMatrix = { onEvent(SurakshaDashboardUiEvent.NavigateToCameraMatrix) }
                )

                Spacer(modifier = Modifier.height(KsetraSpacing.lg))

                SectionHeader(text = "Threat Summary", color = SurakshaShieldBlue)

                Spacer(modifier = Modifier.height(KsetraSpacing.md))

                ThreatSummaryRow(threatCounts = state.threatCounts)

                Spacer(modifier = Modifier.height(KsetraSpacing.xxl))

                SectionHeader(text = "Event Ledger", color = SurakshaShieldBlue)

                Spacer(modifier = Modifier.height(KsetraSpacing.md))

                ThreatLedger(
                    events = state.ledgerEvents,
                    onAcknowledge = { eventId ->
                        onEvent(SurakshaDashboardUiEvent.AcknowledgeEvent(eventId))
                    }
                )

                Spacer(modifier = Modifier.height(KsetraSpacing.xxl))

                if (state.briefing.isNotEmpty()) {
                    SectionHeader(text = "AI Briefing", color = SurakshaShieldBlue)
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    SecurityBriefingCard(briefing = state.briefing)
                    Spacer(modifier = Modifier.height(KsetraSpacing.xxl))
                }

                SectionHeader(text = "Heatmap", color = SurakshaShieldBlue)

                Spacer(modifier = Modifier.height(KsetraSpacing.md))

                ThreatHeatmap(data = state.heatmapData)

                Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))
            }
        }
    }
}

@Composable
private fun SurakshaDashboardHeader(
    onBack: () -> Unit,
    onCameraMatrix: () -> Unit
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
            text = "Suraksha",
            style = MaterialTheme.typography.headlineMedium,
            color = KsetraTextPrimary
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurakshaShieldBlue.copy(alpha = 0.1f))
                .clickable(onClick = onCameraMatrix),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Camera Matrix",
                tint = SurakshaShieldBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun SurakshaDashboardScreenPreview() {
    KsetraTheme {
        SurakshaDashboardContent(
            state = SurakshaDashboardUiState(isLoading = false),
            onEvent = {}
        )
    }
}
