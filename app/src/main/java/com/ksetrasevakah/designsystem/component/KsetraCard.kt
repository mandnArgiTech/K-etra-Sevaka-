package com.ksetrasevakah.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.*

/**
 * Standard card container matching the Kṣetra Sevakaḥ design language.
 */
@Composable
fun KsetraCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(KsetraSpacing.cardRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(KsetraCard, shape)
            .border(1.dp, KsetraBorder, shape)
            .padding(KsetraSpacing.cardPadding)
    ) {
        content()
    }
}

@Preview
@Composable
private fun KsetraCardPreview() {
    KsetraTheme {
        KsetraCard {
            androidx.compose.material3.Text("Card content")
        }
    }
}
