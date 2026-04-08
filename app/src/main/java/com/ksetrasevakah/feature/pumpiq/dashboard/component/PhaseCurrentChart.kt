package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraPhaseB
import com.ksetrasevakah.designsystem.theme.KsetraPhaseR
import com.ksetrasevakah.designsystem.theme.KsetraPhaseY
import com.ksetrasevakah.designsystem.theme.KsetraTheme

@Composable
fun PhaseCurrentChart(
    phaseRData: List<Float>,
    phaseYData: List<Float>,
    phaseBData: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        drawGridLines()
        if (phaseRData.isNotEmpty()) drawLine(phaseRData, KsetraPhaseR)
        if (phaseYData.isNotEmpty()) drawLine(phaseYData, KsetraPhaseY)
        if (phaseBData.isNotEmpty()) drawLine(phaseBData, KsetraPhaseB)
    }
}

private fun DrawScope.drawGridLines() {
    val lineCount = 4
    for (i in 0..lineCount) {
        val y = size.height * i / lineCount
        drawLine(
            color = KsetraBorder.copy(alpha = 0.4f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawLine(data: List<Float>, color: Color) {
    if (data.size < 2) return
    val maxVal = data.max().coerceAtLeast(1f)
    val minVal = data.min().coerceAtMost(0f)
    val range = (maxVal - minVal).coerceAtLeast(0.1f)
    val stepX = size.width / (data.size - 1)
    val path = Path()

    data.forEachIndexed { index, value ->
        val x = stepX * index
        val y = size.height - ((value - minVal) / range) * size.height
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
}

@Preview
@Composable
private fun PhaseCurrentChartPreview() {
    KsetraTheme {
        PhaseCurrentChart(
            phaseRData = listOf(3.8f, 3.9f, 3.7f, 3.85f, 3.82f),
            phaseYData = listOf(3.9f, 3.95f, 3.85f, 3.92f, 3.91f),
            phaseBData = listOf(3.7f, 3.75f, 3.8f, 3.72f, 3.78f)
        )
    }
}
