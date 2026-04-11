package com.ksetrasevakah.feature.setup

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ksetrasevakah.designsystem.theme.KsetraAccentGreen
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.feature.connectivity.ConnectivityCheckViewModel
import com.ksetrasevakah.feature.connectivity.ConnectivityDiagnosticsBody

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SetupScreen(
    onFinished: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val connectivityVm = hiltViewModel<ConnectivityCheckViewModel>()
    val smsPerms = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
        )
    )

    LaunchedEffect(smsPerms.allPermissionsGranted, uiState.phase) {
        if (smsPerms.allPermissionsGranted && uiState.phase == SetupPhase.Permissions) {
            viewModel.onPermissionsGranted()
        }
    }

    LaunchedEffect(uiState.phase) {
        if (uiState.phase == SetupPhase.Permissions && !smsPerms.allPermissionsGranted) {
            smsPerms.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(uiState.phase, smsPerms.allPermissionsGranted) {
        if (uiState.phase == SetupPhase.FundamentalsDiagnostics && smsPerms.allPermissionsGranted) {
            connectivityVm.refresh()
        }
    }

    val scroll = rememberScrollState()
    val scheme = MaterialTheme.colorScheme

    if (uiState.phase == SetupPhase.FundamentalsDiagnostics) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KsetraDarkBackground)
                .padding(horizontal = KsetraSpacing.screenPadding)
        ) {
            Spacer(modifier = Modifier.height(KsetraSpacing.lg))
            Text(
                text = "Kṣetra Sevakaḥ",
                style = MaterialTheme.typography.headlineMedium,
                color = KsetraTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.sm))
            Text(
                text = "First-time setup",
                style = MaterialTheme.typography.titleLarge,
                color = KsetraTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.md))
            Text(
                text = "Before downloading large AI models, confirm the app can read panel SMS " +
                    "and (for Suraksha) intercept camera notifications. Without this data, " +
                    "those downloads are not useful.",
                style = MaterialTheme.typography.bodyLarge,
                color = KsetraTextSecondary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.md))

            ConnectivityDiagnosticsBody(
                viewModel = connectivityVm,
                smsPermissions = smsPerms,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                embeddedHeaderTitle = "Step 2: Verify SMS & notifications"
            )

            Spacer(modifier = Modifier.height(KsetraSpacing.md))
            Text(
                text = "When SMS matching and Tapo listener look correct, continue. " +
                    "You can fix issues above, then tap refresh (↻).",
                style = MaterialTheme.typography.bodyMedium,
                color = KsetraTextSecondary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.md))
            Button(
                onClick = { viewModel.continueAfterFundamentals() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = scheme.primary,
                    contentColor = scheme.onPrimary
                )
            ) {
                Text(
                    text = "Continue to AI model download",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(KsetraSpacing.lg))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KsetraDarkBackground)
                .verticalScroll(scroll)
                .padding(horizontal = KsetraSpacing.screenPadding, vertical = KsetraSpacing.xxl),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kṣetra Sevakaḥ",
                style = MaterialTheme.typography.headlineMedium,
                color = KsetraTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.md))
            Text(
                text = "First-time setup",
                style = MaterialTheme.typography.titleLarge,
                color = KsetraTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))

            when (uiState.phase) {
                SetupPhase.Permissions -> {
                    Text(
                        text = "Grant SMS permissions so the app can read panel messages and control the motor.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = KsetraSpacing.lg)
                    )
                    Button(
                        onClick = { smsPerms.launchMultiplePermissionRequest() },
                        enabled = !smsPerms.allPermissionsGranted,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primary,
                            contentColor = scheme.onPrimary,
                            disabledContainerColor = scheme.surfaceVariant,
                            disabledContentColor = scheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = "Grant SMS permissions",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                SetupPhase.FundamentalsDiagnostics -> Unit
                SetupPhase.DownloadModels -> {
                    val hfSaved by viewModel.huggingFaceReadToken.collectAsStateWithLifecycle()
                    var hfInput by remember { mutableStateOf("") }
                    LaunchedEffect(hfSaved) {
                        if (hfInput.isEmpty() && hfSaved.isNotEmpty()) {
                            hfInput = hfSaved
                        }
                    }
                    Text(
                        text = "Large on-device models (LiteRT chat + ONNX embeddings) download next " +
                            "(Wi‑Fi recommended). This step runs only after SMS and notification checks.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = KsetraSpacing.sm)
                    )
                    Text(
                        text = "Gemma is gated on Hugging Face. Paste a free read token below " +
                            "(huggingface.co/settings/tokens) and accept the Gemma license on the model page, " +
                            "then tap Download.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KsetraTextSecondary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = KsetraSpacing.sm)
                    )
                    OutlinedTextField(
                        value = hfInput,
                        onValueChange = { hfInput = it },
                        label = { Text("Hugging Face read token") },
                        placeholder = { Text("hf_…") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = KsetraSpacing.md)
                    )
                    Text(
                        text = uiState.statusMessage.ifEmpty { "Ready to download on-device models." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = KsetraTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = KsetraSpacing.md)
                    )
                    if (uiState.isWorking) {
                        SetupProgressBar(
                            indeterminate = uiState.downloadIndeterminate,
                            progress = uiState.downloadProgress
                        )
                    }
                    Spacer(modifier = Modifier.height(KsetraSpacing.lg))
                    Button(
                        onClick = { viewModel.startDownloadsAndIngestion(hfInput) },
                        enabled = !uiState.isWorking && viewModel.hasRequiredPermissions(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primary,
                            contentColor = scheme.onPrimary,
                            disabledContainerColor = scheme.surfaceVariant,
                            disabledContentColor = scheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = "Download models & ingest SMS",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                SetupPhase.IngestSms -> {
                    Text(
                        text = uiState.statusMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    SetupProgressBar(
                        indeterminate = uiState.downloadIndeterminate,
                        progress = uiState.downloadProgress
                    )
                }
                SetupPhase.Done -> {
                    Text(
                        text = uiState.statusMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = KsetraAccentGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = KsetraSpacing.lg)
                    )
                    Button(
                        onClick = onFinished,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primary,
                            contentColor = scheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Continue to app",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            uiState.error?.let { err ->
                Spacer(modifier = Modifier.height(KsetraSpacing.lg))
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.phase == SetupPhase.Done || uiState.canContinue) {
                    Spacer(modifier = Modifier.height(KsetraSpacing.md))
                    Button(
                        onClick = onFinished,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primary,
                            contentColor = scheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Continue anyway",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupProgressBar(indeterminate: Boolean, progress: Float) {
    val scheme = MaterialTheme.colorScheme
    val mod = Modifier
        .fillMaxWidth()
        .height(10.dp)
    if (indeterminate) {
        LinearProgressIndicator(
            modifier = mod,
            color = scheme.primary,
            trackColor = scheme.surfaceVariant
        )
    } else {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = mod,
            color = scheme.primary,
            trackColor = scheme.surfaceVariant,
            drawStopIndicator = {}
        )
    }
}
