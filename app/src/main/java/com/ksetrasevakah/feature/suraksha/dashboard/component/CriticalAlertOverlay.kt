package com.ksetrasevakah.feature.suraksha.dashboard.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.SurakshaAlertCriticalBg
import com.ksetrasevakah.designsystem.theme.SurakshaAlertCriticalBorder
import com.ksetrasevakah.designsystem.theme.SurakshaAlertCriticalPulse
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent

@Composable
fun CriticalAlertOverlay(
    event: SecurityEvent?,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && event != null,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SurakshaAlertCriticalBg.copy(alpha = 0.85f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KsetraSpacing.xxl)
                    .border(
                        width = 2.dp,
                        color = SurakshaAlertCriticalBorder,
                        shape = RoundedCornerShape(KsetraSpacing.cardRadius)
                    )
                    .padding(KsetraSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CRITICAL ALERT",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = SurakshaAlertCriticalPulse,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(pulseAlpha)
                )

                Spacer(modifier = Modifier.height(KsetraSpacing.lg))

                event?.let {
                    Text(
                        text = it.eventType.name.replace("_", " "),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = KsetraTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(KsetraSpacing.md))

                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraTextPrimary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(KsetraSpacing.xxl))

                    Text(
                        text = "Tap to dismiss",
                        style = MaterialTheme.typography.labelMedium,
                        color = KsetraTextPrimary.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
