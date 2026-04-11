package com.ksetrasevakah.core.network

import com.ksetrasevakah.BuildConfig
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe bearer token for huggingface.co downloads (gated models such as Gemma).
 * Prefers a token saved in DataStore (setup / device); falls back to [BuildConfig] from local.properties at build time.
 */
@Singleton
class HuggingFaceReadTokenProvider @Inject constructor(
    private val appPreferences: AppPreferencesRepository
) {

    @Volatile
    private var cachedToken: String = BuildConfig.HUGGINGFACE_READ_TOKEN.trim()

    /** Reload from preferences so OkHttp picks up tokens saved on the device. */
    suspend fun refreshFromStorage() {
        val stored = appPreferences.getHuggingFaceReadTokenOnce().trim()
        cachedToken = if (stored.isNotEmpty()) {
            stored
        } else {
            BuildConfig.HUGGINGFACE_READ_TOKEN.trim()
        }
    }

    fun authorizationBearerOrNull(): String? =
        cachedToken.trim().takeIf { it.isNotEmpty() }
}
