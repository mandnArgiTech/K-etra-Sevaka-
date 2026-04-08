package com.ksetrasevakah.feature.suraksha.camera.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlueDim
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode

@Composable
fun CameraModeSelector(
    currentMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CameraMode.entries.forEach { mode ->
            val isSelected = mode == currentMode
            val shape = RoundedCornerShape(KsetraSpacing.chipRadius)
            Text(
                text = mode.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) SurakshaShieldBlue else KsetraTextSecondary,
                modifier = Modifier
                    .clip(shape)
                    .background(
                        if (isSelected) SurakshaShieldBlueDim else KsetraCard,
                        shape
                    )
                    .border(
                        1.dp,
                        if (isSelected) SurakshaShieldBlue.copy(alpha = 0.4f) else KsetraBorder,
                        shape
                    )
                    .clickable { onModeSelected(mode) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
