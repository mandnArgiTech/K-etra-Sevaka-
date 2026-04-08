package com.ksetrasevakah.feature.pumpiq.chat.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreenDim
import com.ksetrasevakah.designsystem.theme.KsetraPurple
import com.ksetrasevakah.designsystem.theme.KsetraPurpleDim
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary

private data class QuickQuery(val label: String, val query: String, val isPrediction: Boolean)

private val quickQueries = listOf(
    QuickQuery("Failure prediction", "What is the current failure prediction for my motor?", true),
    QuickQuery("Maintenance schedule", "When is the next recommended maintenance?", true),
    QuickQuery("Lifespan estimate", "What is the estimated remaining lifespan of my motor?", true),
    QuickQuery("Risk assessment", "What are the current risk factors for my pump?", true),
    QuickQuery("Anomaly check", "Are there any anomalies in recent readings?", true),
    QuickQuery("Current readings", "Show me the latest phase current readings", false),
    QuickQuery("Power usage", "What is my motor's power consumption trend?", false),
    QuickQuery("Temperature trend", "How has the motor temperature changed recently?", false),
    QuickQuery("Run time", "How long has the motor been running today?", false),
    QuickQuery("Voltage status", "Is the voltage within normal range?", false),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickQueryChips(
    onChipSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
    ) {
        quickQueries.forEach { chip ->
            val containerColor = if (chip.isPrediction) KsetraPurpleDim else KsetraAccentGreenDim
            val labelColor = if (chip.isPrediction) KsetraPurple else KsetraAccentGreen

            SuggestionChip(
                onClick = { onChipSelected(chip.query) },
                label = {
                    Text(
                        text = chip.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = KsetraTextPrimary
                    )
                },
                shape = RoundedCornerShape(KsetraSpacing.chipRadius),
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = containerColor,
                    labelColor = labelColor
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = labelColor.copy(alpha = 0.3f)
                ),
                modifier = Modifier.padding(end = KsetraSpacing.xs)
            )
        }
    }
}
