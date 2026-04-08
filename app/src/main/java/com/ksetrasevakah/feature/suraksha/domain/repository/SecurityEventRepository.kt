package com.ksetrasevakah.feature.suraksha.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.model.CameraCount
import com.ksetrasevakah.core.database.model.HourlyCount
import com.ksetrasevakah.core.database.model.ThreatCount
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import kotlinx.coroutines.flow.Flow

interface SecurityEventRepository {
    fun observeRecentEvents(since: Long, limit: Int = 50): Flow<Result<List<SecurityEvent>>>
    fun observeEvents(limit: Int = 50): Flow<Result<List<SecurityEvent>>>
    suspend fun getThreatDistribution(since: Long): Result<List<ThreatCount>>
    suspend fun getThreatCounts(): Result<Map<ThreatLevel, Int>>
    suspend fun getHourlyHeatmap(since: Long): Result<List<HourlyCount>>
    suspend fun getActivitySpikes(windowStart: Long, windowEnd: Long): Result<List<CameraCount>>
    suspend fun getEventsForCamera(cameraName: String, limit: Int = 20): Result<List<SecurityEvent>>
    suspend fun getEventsInWindow(windowMs: Long): Result<List<SecurityEvent>>
    suspend fun getUnacknowledgedHighCount(): Result<Int>
    suspend fun getUnacknowledgedCount(): Result<Int>
    suspend fun insert(event: SecurityEvent): Result<Long>
    suspend fun acknowledge(eventId: Long): Result<Unit>
    suspend fun acknowledgeEvent(eventId: Long): Result<Unit>
}
