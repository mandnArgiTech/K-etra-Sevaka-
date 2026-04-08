package com.ksetrasevakah.feature.suraksha.dashboard.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue

@Composable
fun SecurityBriefingCard(
    briefing: String,
    modifier: Modifier = Modifier
) {
    KsetraCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Security Briefing",
                style = MaterialTheme.typography.labelLarge,
                color = SurakshaShieldBlue
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.sm))
            Text(
                text = briefing,
                style = MaterialTheme.typography.bodyMedium,
                color = KsetraTextPrimary
            )
        }
    }
}
