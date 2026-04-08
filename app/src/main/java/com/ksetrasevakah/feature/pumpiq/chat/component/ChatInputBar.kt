package com.ksetrasevakah.feature.pumpiq.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraSurface
import com.ksetrasevakah.designsystem.theme.KsetraTextDisabled
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary

@Composable
fun ChatInputBar(
    onSendMessage: (String) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = modifier
            .background(KsetraSurface)
            .padding(
                horizontal = KsetraSpacing.screenPadding,
                vertical = KsetraSpacing.sm
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Voice input placeholder */ }) {
            Icon(
                Icons.Filled.Call,
                contentDescription = "Voice input",
                tint = KsetraTextSecondary
            )
        }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = KsetraSpacing.xs),
            placeholder = {
                Text("Ask PumpIQ...", color = KsetraTextDisabled)
            },
            enabled = isEnabled,
            singleLine = false,
            maxLines = 4,
            shape = RoundedCornerShape(KsetraSpacing.cardRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KsetraAccentGreen,
                unfocusedBorderColor = KsetraBorder,
                focusedTextColor = KsetraTextPrimary,
                unfocusedTextColor = KsetraTextPrimary,
                cursorColor = KsetraAccentGreen
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isNotBlank()) {
                        onSendMessage(text.trim())
                        text = ""
                    }
                }
            )
        )

        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSendMessage(text.trim())
                    text = ""
                }
            },
            enabled = isEnabled && text.isNotBlank()
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (text.isNotBlank() && isEnabled) KsetraAccentGreen else KsetraTextDisabled
            )
        }
    }
}
