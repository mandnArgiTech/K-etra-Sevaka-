package com.ksetrasevakah.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.designsystem.theme.*

/**
 * LED dot + label that reflects the current motor state.
 */
@Composable
fun MotorStateIndicator(state: MotorState, modifier: Modifier = Modifier) {
    val dotColor = when (state) {
        MotorState.ON -> KsetraAccentGreen
        MotorState.PENDING_START, MotorState.PENDING_STOP -> KsetraAmber
        MotorState.OFF -> KsetraRed
    }

    val pulseAlpha = if (state.isPending) {
        val transition = rememberInfiniteTransition(label = "pulse")
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
            label = "pulseAlpha"
        )
        alpha
    } else {
        1f
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .alpha(pulseAlpha)
                .shadow(if (state.isOn) 6.dp else 0.dp, CircleShape, ambientColor = dotColor, spotColor = dotColor)
                .background(dotColor, CircleShape)
        )
        Text(
            text = state.displayLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = dotColor
        )
    }
}

@Preview
@Composable
private fun MotorStateIndicatorRunningPreview() {
    KsetraTheme { MotorStateIndicator(state = MotorState.ON) }
}

@Preview
@Composable
private fun MotorStateIndicatorOffPreview() {
    KsetraTheme { MotorStateIndicator(state = MotorState.OFF) }
}
