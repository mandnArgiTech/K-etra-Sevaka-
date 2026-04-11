package com.ksetrasevakah.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RootDestination {
    Loading,
    Setup,
    BootDiagnostics,
    ConnectivityFromBoot,
    Main
}

@HiltViewModel
class RootNavViewModel @Inject constructor(
    private val appPreferences: AppPreferencesRepository
) : ViewModel() {

    private val _destination = MutableStateFlow(RootDestination.Loading)
    val destination: StateFlow<RootDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val complete = appPreferences.isFirstRunComplete.first()
            _destination.value = if (complete) {
                RootDestination.BootDiagnostics
            } else {
                RootDestination.Setup
            }
        }
    }

    fun markSetupFinished() {
        viewModelScope.launch {
            appPreferences.setFirstRunComplete(true)
            _destination.value = RootDestination.BootDiagnostics
        }
    }

    fun markBootDiagnosticsDone() {
        _destination.value = RootDestination.Main
    }

    fun openConnectivityFromBoot() {
        _destination.value = RootDestination.ConnectivityFromBoot
    }

    fun backFromBootConnectivity() {
        _destination.value = RootDestination.BootDiagnostics
    }
}
