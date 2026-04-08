package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.component.MotorStateIndicator
import com.ksetrasevakah.designsystem.component.PhaseCurrentMini
import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraAmber
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraPhaseB
import com.ksetrasevakah.designsystem.theme.KsetraPhaseR
import com.ksetrasevakah.designsystem.theme.KsetraPhaseY
import com.ksetrasevakah.designsystem.theme.KsetraRed
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextDisabled
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.KsetraTheme

@Composable
fun MotorControlCard(
    motorState: MotorState,
    phaseR: Float?,
    phaseY: Float?,
    phaseB: Float?,
    sessionStartTime: Long?,
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    KsetraCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MotorStateIndicator(state = motorState)
                StatusDot(motorState = motorState)
            }

            Spacer(modifier = Modifier.height(KsetraSpacing.lg))

            if (motorState.isOn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                ) {
                    PhaseCurrentMini(
                        label = "R",
                        value = formatCurrent(phaseR),
                        color = KsetraPhaseR,
                        modifier = Modifier.weight(1f)
                    )
                    PhaseCurrentMini(
                        label = "Y",
                        value = formatCurrent(phaseY),
                        color = KsetraPhaseY,
                        modifier = Modifier.weight(1f)
                    )
                    PhaseCurrentMini(
                        label = "B",
                        value = formatCurrent(phaseB),
                        color = KsetraPhaseB,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(KsetraSpacing.lg))

                if (sessionStartTime != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Session",
                            style = MaterialTheme.typography.labelLarge,
                            color = KsetraTextSecondary
                        )
                        SessionTimer(startTimeMillis = sessionStartTime)
                    }
                    Spacer(modifier = Modifier.height(KsetraSpacing.lg))
                }
            }

            ControlButton(
                motorState = motorState,
                onStart = onStart,
                onStop = onStop
            )
        }
    }
}

@Composable
private fun StatusDot(motorState: MotorState) {
    val color = when (motorState) {
        MotorState.ON -> KsetraAccentGreen
        MotorState.OFF -> KsetraRed
        MotorState.PENDING_START, MotorState.PENDING_STOP -> KsetraAmber
    }
    Box(
        modifier = Modifier
            .size(12.dp)
            .shadow(4.dp, CircleShape, ambientColor = color, spotColor = color)
            .background(color, CircleShape)
    )
}

@Composable
private fun ControlButton(
    motorState: MotorState,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val shape = RoundedCornerShape(KsetraSpacing.buttonRadius)
    when (motorState) {
        MotorState.OFF -> {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KsetraAccentGreen,
                    contentColor = KsetraDarkBackground
                )
            ) {
                Text("START PUMP")
            }
        }
        MotorState.ON -> {
            Button(
                onClick = onStop,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KsetraRed.copy(alpha = 0.5f), shape),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KsetraRed.copy(alpha = 0.15f),
                    contentColor = KsetraRed
                )
            ) {
                Text("STOP PUMP")
            }
        }
        MotorState.PENDING_START, MotorState.PENDING_STOP -> {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = KsetraAmber.copy(alpha = 0.15f),
                    disabledContentColor = KsetraTextDisabled
                )
            ) {
                Text(if (motorState == MotorState.PENDING_START) "STARTING…" else "STOPPING…")
            }
        }
    }
}

private fun formatCurrent(value: Float?): String =
    if (value != null) String.format("%.2fA", value) else "—"

@Preview
@Composable
private fun MotorControlCardOnPreview() {
    KsetraTheme {
        MotorControlCard(
            motorState = MotorState.ON,
            phaseR = 3.82f,
            phaseY = 3.91f,
            phaseB = 3.78f,
            sessionStartTime = System.currentTimeMillis() - 3_600_000,
            onStart = {},
            onStop = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun MotorControlCardOffPreview() {
    KsetraTheme {
        MotorControlCard(
            motorState = MotorState.OFF,
            phaseR = null,
            phaseY = null,
            phaseB = null,
            sessionStartTime = null,
            onStart = {},
            onStop = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
