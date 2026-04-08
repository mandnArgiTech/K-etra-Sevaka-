package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.feature.pumpiq.dashboard.model.PredictionsUiState

@Composable
fun PredictionCardsSection(
    state: PredictionsUiState,
    modifier: Modifier = Modifier,
    onCardClick: ((PredictionCardType) -> Unit)? = null
) {
    Column(modifier = modifier) {
        SectionHeader(text = "AI Predictions")

        Spacer(modifier = Modifier.height(KsetraSpacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
        ) {
            PredictionCard(
                icon = Icons.Filled.Warning,
                label = "Power Failure",
                value = state.powerFailureTime ?: "--:--",
                subtitle = "predicted dip time",
                riskLevel = state.powerFailureRisk,
                modifier = Modifier.weight(1f),
                onClick = onCardClick?.let { { it(PredictionCardType.POWER_FAILURE) } }
            )
            PredictionCard(
                icon = Icons.Filled.Build,
                label = "Next Fault",
                value = state.nextFaultHours ?: "—",
                subtitle = "estimated hours",
                riskLevel = state.nextFaultRisk,
                modifier = Modifier.weight(1f),
                onClick = onCardClick?.let { { it(PredictionCardType.FAULT) } }
            )
        }

        Spacer(modifier = Modifier.height(KsetraSpacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
        ) {
            PredictionCard(
                icon = Icons.Filled.Person,
                label = "Worker ON",
                value = state.workerOnTime ?: "--:--",
                subtitle = "expected start",
                riskLevel = state.workerOnRisk,
                modifier = Modifier.weight(1f),
                onClick = onCardClick?.let { { it(PredictionCardType.WORKER_ON) } }
            )
            PredictionCard(
                icon = Icons.Filled.Notifications,
                label = "Forgot OFF",
                value = state.forgotOffPercent ?: "—",
                subtitle = "risk today",
                riskLevel = state.forgotOffRisk,
                modifier = Modifier.weight(1f),
                onClick = onCardClick?.let { { it(PredictionCardType.FORGOT_OFF) } }
            )
        }
    }
}

enum class PredictionCardType {
    POWER_FAILURE, FAULT, WORKER_ON, FORGOT_OFF
}

@Preview
@Composable
private fun PredictionCardsSectionPreview() {
    KsetraTheme {
        PredictionCardsSection(
            state = PredictionsUiState(
                powerFailureTime = "15:00",
                powerFailureRisk = RiskLevel.MEDIUM,
                nextFaultHours = "48h",
                nextFaultRisk = RiskLevel.LOW,
                workerOnTime = "06:30",
                workerOnRisk = RiskLevel.LOW,
                forgotOffPercent = "22%",
                forgotOffRisk = RiskLevel.MEDIUM
            )
        )
    }
}
