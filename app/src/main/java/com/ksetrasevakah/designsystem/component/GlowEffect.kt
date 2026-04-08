package com.ksetrasevakah.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraTheme

/**
 * Decorative radial glow effect used behind cards and sections.
 *
 * @param color The glow center color
 * @param size Diameter of the glow circle
 * @param offsetX Horizontal offset from the parent's top-left
 * @param offsetY Vertical offset from the parent's top-left
 * @param opacity Alpha multiplier for the glow
 */
@Composable
fun GlowEffect(
    color: Color = KsetraAccentGreen,
    size: Dp = 200.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    opacity: Float = 0.08f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .alpha(opacity)
            .blur(40.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                    radius = size.value * 0.7f
                ),
                shape = CircleShape
            )
    )
}

@Preview
@Composable
private fun GlowEffectPreview() {
    KsetraTheme {
        GlowEffect(color = KsetraAccentGreen, size = 200.dp, opacity = 0.15f)
    }
}
