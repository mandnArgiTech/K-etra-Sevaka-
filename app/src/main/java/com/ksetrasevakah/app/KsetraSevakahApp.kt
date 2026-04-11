package com.ksetrasevakah.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ksetrasevakah.core.backup.BackupWorker
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.network.HuggingFaceReadTokenProvider
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application entry point with Hilt dependency injection and WorkManager (HiltWorkerFactory).
 */
@HiltAndroidApp
class KsetraSevakahApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var huggingFaceReadTokenProvider: HuggingFaceReadTokenProvider

    private val appInitScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        appInitScope.launch(Dispatchers.IO) {
            huggingFaceReadTokenProvider.refreshFromStorage()
        }
        scheduleDailyBackup()
    }

    private fun scheduleDailyBackup() {
        val initialDelayMs = initialDelayToNextHour(Constants.BACKUP_SCHEDULE_HOUR)
        val request = PeriodicWorkRequestBuilder<BackupWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            BackupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun initialDelayToNextHour(targetHour: Int): Long {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)
        var next = now.withHour(targetHour).withMinute(0).withSecond(0).withNano(0)
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        return Duration.between(now, next).toMillis()
    }
}
