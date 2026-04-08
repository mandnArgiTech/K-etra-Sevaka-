package com.ksetrasevakah.feature.suraksha.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAmber
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThreatLedgerItem(
    event: SecurityEvent,
    onAcknowledge: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val threatColor = when (event.threatLevel) {
        ThreatLevel.CRITICAL -> KsetraRed
        ThreatLevel.HIGH -> KsetraAmber
        ThreatLevel.MEDIUM -> SurakshaShieldBlue
        ThreatLevel.LOW -> KsetraAccentGreen
        ThreatLevel.NONE -> KsetraAccentGreen
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KsetraSpacing.sm))
            .clickable { if (!event.acknowledged) onAcknowledge(event.id) }
            .padding(vertical = KsetraSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(10.dp)
                .background(threatColor, CircleShape)
        )

        Spacer(modifier = Modifier.width(KsetraSpacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.eventType.name.replace("_", " "),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = KsetraTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = event.description.ifEmpty { event.cameraId },
                style = MaterialTheme.typography.bodySmall,
                color = KsetraTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(KsetraSpacing.sm))

        Text(
            text = formatTimestamp(event.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = KsetraTextSecondary
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
