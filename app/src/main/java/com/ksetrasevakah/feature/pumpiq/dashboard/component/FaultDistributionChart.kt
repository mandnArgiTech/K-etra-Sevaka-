package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAmber
import com.ksetrasevakah.designsystem.theme.KsetraBlue
import com.ksetrasevakah.designsystem.theme.KsetraPurple
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraTheme

data class FaultSlice(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun FaultDistributionChart(
    slices: List<FaultSlice>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (slices.isEmpty()) return@Canvas

        val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
        val diameter = minOf(size.width, size.height) * 0.8f
        val strokeWidth = diameter * 0.18f
        val topLeft = Offset(
            (size.width - diameter) / 2f,
            (size.height - diameter) / 2f
        )

        var startAngle = -90f

        slices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngle
        }
    }
}

@Preview
@Composable
private fun FaultDistributionChartPreview() {
    KsetraTheme {
        FaultDistributionChart(
            slices = listOf(
                FaultSlice("Low Voltage", 12f, KsetraRed),
                FaultSlice("Phase Imbalance", 8f, KsetraAmber),
                FaultSlice("Overload", 4f, KsetraPurple),
                FaultSlice("Dry Run", 2f, KsetraBlue),
                FaultSlice("Normal", 74f, KsetraAccentGreen)
            )
        )
    }
}
