package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.feature.pumpiq.dashboard.model.ChartTab

private val TAB_LABELS = mapOf(
    ChartTab.PHASE to "Phase Currents",
    ChartTab.UPTIME to "Grid",
    ChartTab.FAULTS to "Faults",
    ChartTab.WORKER to "Worker Log",
    ChartTab.POWER to "Power AI"
)

@Composable
fun ChartTabBar(
    activeTab: ChartTab,
    onTabSelected: (ChartTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
    ) {
        ChartTab.entries.forEach { tab ->
            ChartTabChip(
                label = TAB_LABELS[tab] ?: tab.name,
                isSelected = tab == activeTab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun ChartTabChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(KsetraSpacing.chipRadius)
    val bgColor = if (isSelected) KsetraAccentGreen.copy(alpha = 0.15f) else KsetraCard
    val borderColor = if (isSelected) KsetraAccentGreen.copy(alpha = 0.4f) else KsetraBorder
    val textColor = if (isSelected) KsetraTextPrimary else KsetraTextSecondary

    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = textColor,
        modifier = Modifier
            .clip(shape)
            .background(bgColor, shape)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

@Preview
@Composable
private fun ChartTabBarPreview() {
    KsetraTheme {
        ChartTabBar(
            activeTab = ChartTab.PHASE,
            onTabSelected = {}
        )
    }
}
