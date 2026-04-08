package com.ksetrasevakah.feature.suraksha.dashboard.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent

@Composable
fun ThreatLedger(
    events: List<SecurityEvent>,
    onAcknowledge: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    KsetraCard(modifier = modifier.fillMaxWidth()) {
        Column {
            if (events.isEmpty()) {
                Text(
                    text = "No recent security events.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KsetraTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                events.forEachIndexed { index, event ->
                    ThreatLedgerItem(
                        event = event,
                        onAcknowledge = onAcknowledge
                    )
                    if (index < events.lastIndex) {
                        HorizontalDivider(color = KsetraBorder)
                    }
                }
            }
        }
    }
}
