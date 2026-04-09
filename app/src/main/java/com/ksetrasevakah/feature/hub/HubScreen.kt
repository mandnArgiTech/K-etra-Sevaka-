package com.ksetrasevakah.feature.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ksetrasevakah.designsystem.component.GlowEffect
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextDisabled
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue

@Composable
fun HubScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToSuraksha: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HubViewModel = hiltViewModel()
) {
    val hubState by viewModel.uiState.collectAsStateWithLifecycle()
    HubContent(
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToSuraksha = onNavigateToSuraksha,
        onNavigateToSettings = onNavigateToSettings,
        surakshaUnacknowledgedAlerts = hubState.unacknowledgedHighAlerts
    )
}

@Composable
private fun HubContent(
    onNavigateToDashboard: () -> Unit,
    onNavigateToSuraksha: () -> Unit,
    onNavigateToSettings: () -> Unit,
    surakshaUnacknowledgedAlerts: Int = 0
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
    ) {
        GlowEffect(
            color = KsetraAccentGreen,
            size = 300.dp,
            offsetX = (-50).dp,
            offsetY = (-50).dp,
            opacity = 0.06f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(KsetraSpacing.screenPadding)
        ) {
            Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))

            Text(
                text = "Kṣetra Sevakaḥ",
                style = MaterialTheme.typography.displayLarge,
                color = KsetraTextPrimary
            )

            Text(
                text = "Farm Intelligence Platform",
                style = MaterialTheme.typography.bodyMedium,
                color = KsetraTextSecondary,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))

            SectionHeader(text = "Modules")

            Spacer(modifier = Modifier.height(KsetraSpacing.md))

            ModuleCard(
                title = "PumpIQ",
                description = "Motor monitoring, SMS control & AI predictions",
                isActive = true,
                onClick = onNavigateToDashboard
            )

            Spacer(modifier = Modifier.height(KsetraSpacing.md))

            ModuleCard(
                title = "Suraksha",
                description = "Farm security monitoring & threat detection",
                isActive = true,
                accentColor = SurakshaShieldBlue,
                badgeCount = surakshaUnacknowledgedAlerts,
                onClick = onNavigateToSuraksha
            )

            Spacer(modifier = Modifier.height(KsetraSpacing.md))

            ModuleCard(
                title = "SoilAnalytics",
                description = "Soil health monitoring & nutrient analysis",
                isActive = false,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))

            SectionHeader(text = "System")

            Spacer(modifier = Modifier.height(KsetraSpacing.md))

            SystemRow(
                title = "Backup",
                subtitle = "Google Drive sync",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(KsetraSpacing.sm))

            SystemRow(
                title = "Settings",
                subtitle = "Panel number, notifications",
                onClick = onNavigateToSettings
            )

            Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    description: String,
    isActive: Boolean,
    accentColor: Color = KsetraAccentGreen,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    KsetraCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isActive) Modifier.clickable(onClick = onClick) else Modifier.alpha(0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = KsetraTextPrimary
                    )
                    if (badgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .background(KsetraRed, CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (!isActive) {
                        ComingSoonBadge()
                    }
                }
                Spacer(modifier = Modifier.height(KsetraSpacing.xs))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KsetraTextSecondary
                )
            }
            if (isActive) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open $title",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ComingSoonBadge() {
    val shape = RoundedCornerShape(KsetraSpacing.badgeRadius)
    Text(
        text = "COMING SOON",
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = KsetraTextDisabled,
        modifier = Modifier
            .background(KsetraDarkBackground.copy(alpha = 0.5f), shape)
            .border(1.dp, KsetraTextDisabled.copy(alpha = 0.3f), shape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun SystemRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KsetraSpacing.cardRadius))
            .clickable(onClick = onClick)
            .padding(vertical = KsetraSpacing.md, horizontal = KsetraSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = KsetraTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = KsetraTextSecondary
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(KsetraAccentGreen.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open $title",
                tint = KsetraAccentGreen,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview
@Composable
private fun HubScreenPreview() {
    KsetraTheme {
        HubContent(
            onNavigateToDashboard = {},
            onNavigateToSuraksha = {},
            onNavigateToSettings = {}
        )
    }
}
