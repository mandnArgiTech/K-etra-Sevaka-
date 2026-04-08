package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivitySpikeDetector @Inject constructor(
    private val repository: SecurityEventRepository
) {

    suspend fun isActivitySpike(cameraName: String, timestamp: Long): Boolean {
        val windowStart = timestamp - Constants.ACTIVITY_SPIKE_WINDOW_MS
        val spikesResult = repository.getActivitySpikes(windowStart, timestamp)
        val spikes = spikesResult.getOrNull() ?: return false

        val cameraCount = spikes.find { it.cameraName == cameraName }?.count ?: 0
        return cameraCount >= Constants.ACTIVITY_SPIKE_THRESHOLD
    }

    suspend fun isCoordinatedActivity(timestamp: Long): Boolean {
        val windowStart = timestamp - Constants.COORDINATED_WINDOW_MS
        val spikesResult = repository.getActivitySpikes(windowStart, timestamp)
        val spikes = spikesResult.getOrNull() ?: return false

        val activeCameras = spikes.count { it.count >= 2 }
        return activeCameras >= 2
    }

    suspend fun getRecentSpikeCount(cameraName: String, windowMs: Long): Int {
        val now = System.currentTimeMillis()
        val windowStart = now - windowMs
        val spikesResult = repository.getActivitySpikes(windowStart, now)
        val spikes = spikesResult.getOrNull() ?: return 0
        return spikes.find { it.cameraName == cameraName }?.count ?: 0
    }
}
