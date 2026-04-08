package com.ksetrasevakah.feature.suraksha.dashboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue
import com.ksetrasevakah.feature.suraksha.dashboard.model.HeatmapEntry

@Composable
fun ThreatHeatmap(
    data: List<HeatmapEntry>,
    modifier: Modifier = Modifier
) {
    KsetraCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Activity Heatmap",
                style = MaterialTheme.typography.labelLarge,
                color = SurakshaShieldBlue
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.sm))

            if (data.isEmpty()) {
                Text(
                    text = "Insufficient data for heatmap.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KsetraTextSecondary
                )
            } else {
                val maxCount = data.maxOfOrNull { it.count } ?: 1
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    val cellWidth = size.width / 24f
                    val cellHeight = size.height / 7f

                    data.forEach { entry ->
                        val intensity = entry.count.toFloat() / maxCount
                        val color = SurakshaShieldBlue.copy(alpha = 0.1f + intensity * 0.9f)
                        drawRect(
                            color = color,
                            topLeft = Offset(entry.hour * cellWidth, (entry.dayOfWeek - 1) * cellHeight),
                            size = Size(cellWidth - 1f, cellHeight - 1f)
                        )
                    }
                }
            }
        }
    }
}
