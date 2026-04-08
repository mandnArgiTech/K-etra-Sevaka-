package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraTheme

data class GridReliabilityEntry(
    val label: String,
    val uptimeHours: Float,
    val downtimeHours: Float
)

@Composable
fun GridReliabilityChart(
    entries: List<GridReliabilityEntry>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (entries.isEmpty()) return@Canvas

        val barWidth = size.width / (entries.size * 2f)
        val gap = barWidth * 0.4f
        val maxHours = entries.maxOf { it.uptimeHours + it.downtimeHours }.coerceAtLeast(1f)

        drawLine(
            color = KsetraBorder.copy(alpha = 0.4f),
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 1f
        )

        entries.forEachIndexed { index, entry ->
            val x = (index * 2 + 0.5f) * barWidth + gap * index
            val totalHeight = ((entry.uptimeHours + entry.downtimeHours) / maxHours) * size.height

            val uptimeHeight = (entry.uptimeHours / maxHours) * size.height
            val downtimeHeight = totalHeight - uptimeHeight

            drawRoundRect(
                color = KsetraAccentGreen.copy(alpha = 0.7f),
                topLeft = Offset(x, size.height - uptimeHeight - downtimeHeight),
                size = Size(barWidth, uptimeHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            if (downtimeHeight > 0f) {
                drawRoundRect(
                    color = KsetraRed.copy(alpha = 0.6f),
                    topLeft = Offset(x, size.height - downtimeHeight),
                    size = Size(barWidth, downtimeHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun GridReliabilityChartPreview() {
    KsetraTheme {
        GridReliabilityChart(
            entries = listOf(
                GridReliabilityEntry("Mon", 20f, 4f),
                GridReliabilityEntry("Tue", 22f, 2f),
                GridReliabilityEntry("Wed", 18f, 6f),
                GridReliabilityEntry("Thu", 23f, 1f),
                GridReliabilityEntry("Fri", 21f, 3f)
            )
        )
    }
}
