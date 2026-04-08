package com.ksetrasevakah.feature.suraksha.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAmber
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel

@Composable
fun ThreatSummaryRow(
    threatCounts: Map<ThreatLevel, Int>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
    ) {
        ThreatCountChip(
            label = "CRITICAL",
            count = threatCounts[ThreatLevel.CRITICAL] ?: 0,
            color = KsetraRed,
            modifier = Modifier.weight(1f)
        )
        ThreatCountChip(
            label = "HIGH",
            count = threatCounts[ThreatLevel.HIGH] ?: 0,
            color = KsetraAmber,
            modifier = Modifier.weight(1f)
        )
        ThreatCountChip(
            label = "MEDIUM",
            count = threatCounts[ThreatLevel.MEDIUM] ?: 0,
            color = SurakshaShieldBlue,
            modifier = Modifier.weight(1f)
        )
        ThreatCountChip(
            label = "LOW",
            count = threatCounts[ThreatLevel.LOW] ?: 0,
            color = KsetraAccentGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ThreatCountChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(KsetraSpacing.cardRadius)
    Column(
        modifier = modifier
            .background(KsetraCard, shape)
            .border(1.dp, KsetraBorder, shape)
            .padding(KsetraSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = KsetraTextSecondary
        )
    }
}
