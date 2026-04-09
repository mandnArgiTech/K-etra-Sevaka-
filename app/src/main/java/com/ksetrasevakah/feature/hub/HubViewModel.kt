package com.ksetrasevakah.feature.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HubViewModel @Inject constructor(
    private val securityEventRepository: SecurityEventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HubUiState())
    val uiState: StateFlow<HubUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            securityEventRepository.getUnacknowledgedHighCount().collectLatest { result ->
                when (result) {
                    is Result.Success ->
                        _uiState.update { it.copy(unacknowledgedHighAlerts = result.data) }
                    is Result.Error -> { /* non-fatal */ }
                    is Result.Loading -> { /* no-op */ }
                }
            }
        }
    }

    data class HubUiState(
        val unacknowledgedHighAlerts: Int = 0
    )
}
