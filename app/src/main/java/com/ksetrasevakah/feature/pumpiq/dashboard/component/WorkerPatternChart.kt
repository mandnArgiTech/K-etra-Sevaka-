package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksetrasevakah.designsystem.theme.*

data class WorkerPatternEntry(
    val dateLabel: String,
    val onHour: Float,
    val offHour: Float,
    val forgotOff: Boolean = false
)

@Composable
fun WorkerPatternChart(
    entries: List<WorkerPatternEntry>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        entries.forEach { entry ->
            WorkerPatternRow(entry)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun WorkerPatternRow(entry: WorkerPatternEntry) {
    val barColor = if (entry.forgotOff) KsetraRed.copy(alpha = 0.7f) else KsetraAccentGreen.copy(alpha = 0.6f)
    val labelColor = if (entry.forgotOff) KsetraRed else KsetraTextSecondary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = entry.dateLabel,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor,
            modifier = Modifier.width(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(14.dp)
        ) {
            drawRoundRect(
                color = KsetraBorder.copy(alpha = 0.3f),
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(4f, 4f)
            )

            val totalHours = 24f
            val startFraction = (entry.onHour / totalHours).coerceIn(0f, 1f)
            val endFraction = (entry.offHour / totalHours).coerceIn(0f, 1f)

            if (endFraction > startFraction) {
                val barStart = startFraction * size.width
                val barWidth = (endFraction - startFraction) * size.width

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(barStart, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = formatTime(entry.onHour) + "–" + formatTime(entry.offHour),
            fontSize = 9.sp,
            color = labelColor,
            modifier = Modifier.width(72.dp)
        )
    }
}

private fun formatTime(hourDecimal: Float): String {
    val hour = hourDecimal.toInt()
    val minute = ((hourDecimal - hour) * 60).toInt()
    return "%02d:%02d".format(hour, minute)
}

@Preview
@Composable
private fun WorkerPatternChartPreview() {
    KsetraTheme {
        WorkerPatternChart(
            entries = listOf(
                WorkerPatternEntry("Mon", 6.5f, 18.0f),
                WorkerPatternEntry("Tue", 6.0f, 17.5f),
                WorkerPatternEntry("Wed", 7.0f, 23.5f, forgotOff = true),
                WorkerPatternEntry("Thu", 6.5f, 18.0f),
                WorkerPatternEntry("Fri", 6.0f, 17.0f)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
