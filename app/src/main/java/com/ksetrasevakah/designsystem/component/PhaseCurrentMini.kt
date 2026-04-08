package com.ksetrasevakah.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksetrasevakah.designsystem.theme.*

/**
 * Compact phase current readout (one phase: R, Y, or B).
 */
@Composable
fun PhaseCurrentMini(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .background(Color(0x08FFFFFF), shape)
            .border(1.dp, color.copy(alpha = 0.13f), shape)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = "$label Phase",
            fontSize = 10.sp,
            color = KsetraTextSecondary
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMonoFamily,
            color = color
        )
    }
}

@Preview
@Composable
private fun PhaseCurrentMiniPreview() {
    KsetraTheme { PhaseCurrentMini(label = "R", value = "3.82A", color = KsetraPhaseR) }
}
