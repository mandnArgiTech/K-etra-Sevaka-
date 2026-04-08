package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAmber
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraTheme

data class PowerForecastPoint(
    val hourOffset: Float,
    val voltage: Float
)

@Composable
fun PowerForecastChart(
    actual: List<PowerForecastPoint>,
    predicted: List<PowerForecastPoint>,
    modifier: Modifier = Modifier,
    dangerVoltage: Float = 200f,
    maxVoltage: Float = 260f
) {
    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val allPoints = actual + predicted
        if (allPoints.isEmpty()) return@Canvas

        val minHour = allPoints.minOf { it.hourOffset }
        val maxHour = allPoints.maxOf { it.hourOffset }
        val hourRange = (maxHour - minHour).coerceAtLeast(1f)

        fun toX(hour: Float) = ((hour - minHour) / hourRange) * size.width
        fun toY(voltage: Float) = size.height - ((voltage / maxVoltage) * size.height)

        drawLine(
            color = KsetraBorder.copy(alpha = 0.3f),
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 1f
        )

        val dangerY = toY(dangerVoltage)
        drawLine(
            color = KsetraRed.copy(alpha = 0.6f),
            start = Offset(0f, dangerY),
            end = Offset(size.width, dangerY),
            strokeWidth = 1.5f,
            pathEffect = dashedEffect
        )

        if (actual.size >= 2) {
            val sorted = actual.sortedBy { it.hourOffset }
            for (i in 0 until sorted.size - 1) {
                drawLine(
                    color = KsetraAccentGreen,
                    start = Offset(toX(sorted[i].hourOffset), toY(sorted[i].voltage)),
                    end = Offset(toX(sorted[i + 1].hourOffset), toY(sorted[i + 1].voltage)),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )
            }

            sorted.forEach { point ->
                drawCircle(
                    color = KsetraAccentGreen,
                    radius = 3f,
                    center = Offset(toX(point.hourOffset), toY(point.voltage))
                )
            }
        }

        if (predicted.size >= 2) {
            val sorted = predicted.sortedBy { it.hourOffset }
            for (i in 0 until sorted.size - 1) {
                drawLine(
                    color = KsetraAmber,
                    start = Offset(toX(sorted[i].hourOffset), toY(sorted[i].voltage)),
                    end = Offset(toX(sorted[i + 1].hourOffset), toY(sorted[i + 1].voltage)),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round,
                    pathEffect = dashedEffect
                )
            }

            sorted.forEach { point ->
                drawCircle(
                    color = KsetraAmber.copy(alpha = 0.7f),
                    radius = 3f,
                    center = Offset(toX(point.hourOffset), toY(point.voltage))
                )
            }
        }

        if (actual.isNotEmpty() && predicted.isNotEmpty()) {
            val lastActual = actual.maxByOrNull { it.hourOffset }
            val firstPredicted = predicted.minByOrNull { it.hourOffset }
            if (lastActual != null && firstPredicted != null) {
                drawLine(
                    color = KsetraAmber.copy(alpha = 0.5f),
                    start = Offset(toX(lastActual.hourOffset), toY(lastActual.voltage)),
                    end = Offset(toX(firstPredicted.hourOffset), toY(firstPredicted.voltage)),
                    strokeWidth = 1.5f,
                    cap = StrokeCap.Round,
                    pathEffect = dashedEffect
                )
            }
        }
    }
}

@Preview
@Composable
private fun PowerForecastChartPreview() {
    KsetraTheme {
        PowerForecastChart(
            actual = listOf(
                PowerForecastPoint(0f, 235f),
                PowerForecastPoint(1f, 230f),
                PowerForecastPoint(2f, 228f),
                PowerForecastPoint(3f, 225f),
                PowerForecastPoint(4f, 220f)
            ),
            predicted = listOf(
                PowerForecastPoint(5f, 215f),
                PowerForecastPoint(6f, 208f),
                PowerForecastPoint(7f, 195f),
                PowerForecastPoint(8f, 190f)
            )
        )
    }
}
