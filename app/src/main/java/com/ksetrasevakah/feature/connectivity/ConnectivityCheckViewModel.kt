package com.ksetrasevakah.feature.connectivity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import com.ksetrasevakah.core.sms.SmsHistoryReader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SmsCheckState(
    val hasPermission: Boolean = false,
    val isScanning: Boolean = false,
    val panelNumber: String = "",
    /** Messages from the configured panel number */
    val panelMessageCount: Int = 0,
    val oldestPanelMessageMs: Long? = null,
    val newestPanelMessageMs: Long? = null,
    /** All unique senders discovered in the inbox */
    val allSenders: List<SmsHistoryReader.SenderInfo> = emptyList(),
    val scanError: String? = null,
    val showNumberPicker: Boolean = false
)

data class AppInfo(
    val packageName: String,
    val label: String
)

data class NotifCheckState(
    val listenerGranted: Boolean = false,
    val watchedPackage: String = "",
    val watchedAppLabel: String = "",
    val installedApps: List<AppInfo> = emptyList(),
    val isLoadingApps: Boolean = false,
    val showAppPicker: Boolean = false
)

data class ConnectivityUiState(
    val sms: SmsCheckState = SmsCheckState(),
    val notif: NotifCheckState = NotifCheckState()
)

@HiltViewModel
class ConnectivityCheckViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val smsHistoryReader: SmsHistoryReader,
    private val appPreferences: AppPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectivityUiState())
    val state: StateFlow<ConnectivityUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val panelNumber = appPreferences.getPanelNumber()
            val watchedPkg = appPreferences.getTapoPackageName()
            val hasSmsPerm = ContextCompat.checkSelfPermission(
                appContext, Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED

            val listenerGranted = isNotificationListenerGranted()
            val watchedLabel = labelForPackage(watchedPkg)

            _state.update { s ->
                s.copy(
                    sms = s.sms.copy(
                        hasPermission = hasSmsPerm,
                        panelNumber = panelNumber
                    ),
                    notif = s.notif.copy(
                        listenerGranted = listenerGranted,
                        watchedPackage = watchedPkg,
                        watchedAppLabel = watchedLabel
                    )
                )
            }

            if (hasSmsPerm) {
                countPanelMessages(panelNumber)
            }
        }
    }

    /** Scans the inbox for all unique senders and checks the current panel number. */
    fun scanInbox() {
        viewModelScope.launch {
            _state.update { it.copy(sms = it.sms.copy(isScanning = true, scanError = null)) }
            try {
                val senders = smsHistoryReader.readAllInboxSenders()
                val panelNumber = _state.value.sms.panelNumber
                val panelMsgs = smsHistoryReader.readMessagesFromNumber(panelNumber)
                _state.update { s ->
                    s.copy(
                        sms = s.sms.copy(
                            isScanning = false,
                            allSenders = senders,
                            panelMessageCount = panelMsgs.size,
                            oldestPanelMessageMs = panelMsgs.minOfOrNull { it.timestamp },
                            newestPanelMessageMs = panelMsgs.maxOfOrNull { it.timestamp }
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(sms = it.sms.copy(isScanning = false, scanError = e.message)) }
            }
        }
    }

    fun showNumberPicker() {
        if (_state.value.sms.allSenders.isEmpty()) {
            scanInbox()
        }
        _state.update { it.copy(sms = it.sms.copy(showNumberPicker = true)) }
    }

    fun dismissNumberPicker() {
        _state.update { it.copy(sms = it.sms.copy(showNumberPicker = false)) }
    }

    fun selectPanelNumber(number: String) {
        viewModelScope.launch {
            appPreferences.setPanelNumber(number)
            _state.update { s ->
                s.copy(sms = s.sms.copy(panelNumber = number, showNumberPicker = false))
            }
            countPanelMessages(number)
        }
    }

    fun showAppPicker() {
        _state.update { it.copy(notif = it.notif.copy(showAppPicker = true, isLoadingApps = true)) }
        viewModelScope.launch {
            val apps = loadInstalledAppsWithNotifications()
            _state.update { it.copy(notif = it.notif.copy(installedApps = apps, isLoadingApps = false)) }
        }
    }

    fun dismissAppPicker() {
        _state.update { it.copy(notif = it.notif.copy(showAppPicker = false)) }
    }

    fun selectWatchedApp(packageName: String) {
        viewModelScope.launch {
            appPreferences.setTapoPackageName(packageName)
            val label = labelForPackage(packageName)
            _state.update { s ->
                s.copy(
                    notif = s.notif.copy(
                        watchedPackage = packageName,
                        watchedAppLabel = label,
                        showAppPicker = false
                    )
                )
            }
        }
    }

    private suspend fun countPanelMessages(number: String) {
        try {
            val msgs = smsHistoryReader.readMessagesFromNumber(number)
            _state.update { s ->
                s.copy(
                    sms = s.sms.copy(
                        panelMessageCount = msgs.size,
                        oldestPanelMessageMs = msgs.minOfOrNull { it.timestamp },
                        newestPanelMessageMs = msgs.maxOfOrNull { it.timestamp }
                    )
                )
            }
        } catch (_: Exception) {
        }
    }

    private fun isNotificationListenerGranted(): Boolean {
        val flat = Settings.Secure.getString(
            appContext.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return flat.contains(appContext.packageName)
    }

    private fun loadInstalledAppsWithNotifications(): List<AppInfo> {
        val pm = appContext.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { info ->
                val label = pm.getApplicationLabel(info).toString()
                AppInfo(packageName = info.packageName, label = label)
            }
            .sortedWith(compareBy({ it.packageName != Constants.TAPO_PACKAGE_NAME }, { it.label }))
    }

    private fun labelForPackage(pkg: String): String = try {
        val pm = appContext.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    } catch (_: Exception) {
        pkg
    }
}
