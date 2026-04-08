package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksetrasevakah.designsystem.component.RiskBadge
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.designsystem.theme.*

@Composable
fun PredictionCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String,
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val gradientColors = when (riskLevel) {
        RiskLevel.LOW -> listOf(Color(0x104ADE80), Color(0x054ADE80))
        RiskLevel.MEDIUM -> listOf(Color(0x10F59E0B), Color(0x05F59E0B))
        RiskLevel.HIGH -> listOf(Color(0x18EF4444), Color(0x08EF4444))
        RiskLevel.CRITICAL -> listOf(Color(0x22EF4444), Color(0x10EF4444))
    }

    val shape = RoundedCornerShape(KsetraSpacing.cardRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.verticalGradient(gradientColors), shape)
            .border(1.dp, KsetraBorder, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(KsetraSpacing.md)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = KsetraAccentGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = KsetraTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KsetraTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = KsetraTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            RiskBadge(level = riskLevel, small = true)
        }
    }
}

@Preview
@Composable
private fun PredictionCardPreview() {
    KsetraTheme {
        PredictionCard(
            icon = Icons.Filled.Warning,
            label = "Power Failure",
            value = "15:00",
            subtitle = "3 dips in 14 days",
            riskLevel = RiskLevel.MEDIUM,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
