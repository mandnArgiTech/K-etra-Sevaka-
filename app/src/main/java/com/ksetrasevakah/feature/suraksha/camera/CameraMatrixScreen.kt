package com.ksetrasevakah.feature.suraksha.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ksetrasevakah.designsystem.component.GlowEffect
import com.ksetrasevakah.designsystem.component.KsetraCard
import com.ksetrasevakah.designsystem.component.SectionHeader
import com.ksetrasevakah.designsystem.theme.KsetraBorder
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import com.ksetrasevakah.designsystem.theme.SurakshaShieldBlue
import com.ksetrasevakah.feature.suraksha.camera.component.CameraConfigRow
import com.ksetrasevakah.feature.suraksha.camera.model.CameraMatrixUiEvent
import com.ksetrasevakah.feature.suraksha.camera.model.CameraMatrixUiState

@Composable
fun CameraMatrixScreen(
    onNavigateBack: () -> Unit,
    viewModel: CameraMatrixViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { target ->
            when (target) {
                is CameraMatrixViewModel.NavigationTarget.Back -> onNavigateBack()
            }
        }
    }

    CameraMatrixContent(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun CameraMatrixContent(
    state: CameraMatrixUiState,
    onEvent: (CameraMatrixUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KsetraDarkBackground)
    ) {
        GlowEffect(
            color = SurakshaShieldBlue,
            size = 250.dp,
            offsetX = (-40).dp,
            offsetY = (-30).dp,
            opacity = 0.05f
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = SurakshaShieldBlue
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(KsetraSpacing.screenPadding)
            ) {
                CameraMatrixHeader(onBack = { onEvent(CameraMatrixUiEvent.NavigateBack) })

                Spacer(modifier = Modifier.height(KsetraSpacing.lg))

                CameraStatusSummary(
                    onlineCount = state.onlineCount,
                    offlineCount = state.offlineCount
                )

                Spacer(modifier = Modifier.height(KsetraSpacing.xxl))

                SectionHeader(text = "Cameras", color = SurakshaShieldBlue)

                Spacer(modifier = Modifier.height(KsetraSpacing.md))

                KsetraCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        if (state.cameras.isEmpty()) {
                            Text(
                                text = "No cameras configured.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = KsetraTextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            state.cameras.forEachIndexed { index, camera ->
                                CameraConfigRow(
                                    camera = camera,
                                    onModeChanged = { mode ->
                                        onEvent(CameraMatrixUiEvent.ChangeCameraMode(camera.id, mode))
                                    }
                                )
                                if (index < state.cameras.lastIndex) {
                                    HorizontalDivider(color = KsetraBorder)
                                    Spacer(modifier = Modifier.height(KsetraSpacing.sm))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(KsetraSpacing.xxxl))
            }
        }
    }
}

@Composable
private fun CameraMatrixHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = KsetraTextPrimary
            )
        }

        Text(
            text = "Camera Matrix",
            style = MaterialTheme.typography.headlineMedium,
            color = KsetraTextPrimary
        )
    }
}

@Composable
private fun CameraStatusSummary(onlineCount: Int, offlineCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(KsetraSpacing.md)
    ) {
        KsetraCard(modifier = Modifier.weight(1f)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = onlineCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = SurakshaShieldBlue
                )
                Text(
                    text = "Online",
                    style = MaterialTheme.typography.labelSmall,
                    color = KsetraTextSecondary
                )
            }
        }
        KsetraCard(modifier = Modifier.weight(1f)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = offlineCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = KsetraTextSecondary
                )
                Text(
                    text = "Offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = KsetraTextSecondary
                )
            }
        }
    }
}

@Preview
@Composable
private fun CameraMatrixScreenPreview() {
    KsetraTheme {
        CameraMatrixContent(
            state = CameraMatrixUiState(isLoading = false),
            onEvent = {}
        )
    }
}
