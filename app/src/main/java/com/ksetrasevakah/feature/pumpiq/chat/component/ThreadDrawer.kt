package com.ksetrasevakah.feature.pumpiq.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraCard
import com.ksetrasevakah.designsystem.theme.KsetraCardHover
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatThread

@Composable
fun ThreadDrawer(
    threads: List<ChatThread>,
    currentThreadId: Long?,
    onThreadSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(280.dp),
        drawerContainerColor = KsetraDarkBackground
    ) {
        Text(
            text = "CHAT HISTORY",
            style = MaterialTheme.typography.labelLarge,
            color = KsetraAccentGreen,
            modifier = Modifier.padding(KsetraSpacing.screenPadding)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(threads, key = { it.id }) { thread ->
                val isSelected = thread.id == currentThreadId
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KsetraSpacing.sm, vertical = KsetraSpacing.xs)
                        .clip(RoundedCornerShape(KsetraSpacing.sm))
                        .background(if (isSelected) KsetraCardHover else KsetraCard)
                        .clickable { onThreadSelected(thread.id) }
                        .padding(KsetraSpacing.md)
                ) {
                    Text(
                        text = thread.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.xs))
                    Text(
                        text = thread.preview,
                        style = MaterialTheme.typography.bodySmall,
                        color = KsetraTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
