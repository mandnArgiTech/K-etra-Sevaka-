package com.ksetrasevakah.feature.pumpiq.dashboard.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.JetBrainsMonoFamily
import kotlinx.coroutines.delay

@Composable
fun SessionTimer(
    startTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    var elapsed by remember { mutableLongStateOf(System.currentTimeMillis() - startTimeMillis) }

    LaunchedEffect(startTimeMillis) {
        while (true) {
            elapsed = System.currentTimeMillis() - startTimeMillis
            delay(1000L)
        }
    }

    val totalSeconds = elapsed / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Text(
        text = formatted,
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = KsetraAccentGreen,
        modifier = modifier
    )
}
