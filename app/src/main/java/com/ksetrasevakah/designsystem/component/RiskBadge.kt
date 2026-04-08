package com.ksetrasevakah.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.designsystem.theme.*

/**
 * Pill-shaped badge displaying a risk/threat level.
 *
 * @param level The risk level to display
 * @param small When true, uses compact sizing
 */
@Composable
fun RiskBadge(level: RiskLevel, small: Boolean = false, modifier: Modifier = Modifier) {
    val (bg, text, border) = riskColors(level)
    val shape = RoundedCornerShape(KsetraSpacing.badgeRadius)
    Text(
        text = level.name,
        fontSize = if (small) 9.sp else 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = text,
        modifier = modifier
            .background(bg, shape)
            .border(1.dp, border, shape)
            .padding(
                horizontal = if (small) 8.dp else 12.dp,
                vertical = if (small) 2.dp else 4.dp
            )
    )
}

private fun riskColors(level: RiskLevel): Triple<Color, Color, Color> = when (level) {
    RiskLevel.LOW -> Triple(Color(0x204ADE80), KsetraAccentGreen, Color(0x444ADE80))
    RiskLevel.MEDIUM -> Triple(Color(0x20F59E0B), KsetraAmber, Color(0x44F59E0B))
    RiskLevel.HIGH -> Triple(Color(0x20EF4444), KsetraRed, Color(0x44EF4444))
    RiskLevel.CRITICAL -> Triple(Color(0x30EF4444), Color(0xFFFF6B6B), Color(0x66EF4444))
}

@Preview
@Composable
private fun RiskBadgePreview() {
    KsetraTheme {
        RiskBadge(level = RiskLevel.HIGH)
    }
}
