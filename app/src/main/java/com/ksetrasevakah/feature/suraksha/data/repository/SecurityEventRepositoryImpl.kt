package com.ksetrasevakah.feature.suraksha.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.SecurityEventDao
import com.ksetrasevakah.core.database.entity.SecurityEventEntity
import com.ksetrasevakah.core.database.model.CameraCount
import com.ksetrasevakah.core.database.model.HourlyCount
import com.ksetrasevakah.core.database.model.ThreatCount
import com.ksetrasevakah.feature.suraksha.domain.model.EventType
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SecurityEventRepositoryImpl @Inject constructor(
    private val dao: SecurityEventDao
) : SecurityEventRepository {

    override fun observeRecentEvents(since: Long, limit: Int): Flow<Result<List<SecurityEvent>>> =
        dao.getRecentEvents(since, limit).map { entities ->
            try {
                Result.Success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe security events", e)
            }
        }

    override fun observeEvents(limit: Int): Flow<Result<List<SecurityEvent>>> {
        val since = System.currentTimeMillis() - (24L * 60L * 60L * 1000L)
        return observeRecentEvents(since, limit)
    }

    override suspend fun getThreatDistribution(since: Long): Result<List<ThreatCount>> =
        try {
            Result.Success(dao.getThreatDistribution(since))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get threat distribution", e)
        }

    override suspend fun getThreatCounts(): Result<Map<ThreatLevel, Int>> =
        try {
            val since = System.currentTimeMillis() - (24L * 60L * 60L * 1000L)
            val distribution = dao.getThreatDistribution(since)
            val map = distribution.associate { ThreatLevel.fromString(it.threatLevel) to it.count }
            Result.Success(map)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get threat counts", e)
        }

    override suspend fun getHourlyHeatmap(since: Long): Result<List<HourlyCount>> =
        try {
            Result.Success(dao.getHourlyHeatmap(since))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get hourly heatmap", e)
        }

    override suspend fun getActivitySpikes(
        windowStart: Long,
        windowEnd: Long
    ): Result<List<CameraCount>> =
        try {
            Result.Success(dao.getActivitySpikes(windowStart, windowEnd))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get activity spikes", e)
        }

    override suspend fun getEventsForCamera(
        cameraName: String,
        limit: Int
    ): Result<List<SecurityEvent>> =
        try {
            Result.Success(dao.getEventsForCamera(cameraName, limit).map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get events for camera", e)
        }

    override suspend fun getEventsInWindow(windowMs: Long): Result<List<SecurityEvent>> =
        try {
            val since = System.currentTimeMillis() - windowMs
            val allRecent = dao.getRecentEventsList(since)
            Result.Success(allRecent.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get events in window", e)
        }

    override suspend fun getUnacknowledgedHighCount(): Result<Int> =
        try {
            Result.Success(dao.getUnacknowledgedHighCount())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get unacknowledged count", e)
        }

    override suspend fun getUnacknowledgedCount(): Result<Int> = getUnacknowledgedHighCount()

    override suspend fun insert(event: SecurityEvent): Result<Long> =
        try {
            Result.Success(dao.insert(event.toEntity()))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to insert security event", e)
        }

    override suspend fun acknowledge(eventId: Long): Result<Unit> =
        try {
            dao.acknowledge(eventId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to acknowledge event", e)
        }

    override suspend fun acknowledgeEvent(eventId: Long): Result<Unit> = acknowledge(eventId)

    private fun SecurityEventEntity.toDomain() = SecurityEvent(
        id = id,
        cameraName = cameraName,
        eventType = EventType.fromString(eventType),
        threatLevel = ThreatLevel.fromString(threatLevel),
        confidence = confidence,
        originTimestamp = originTimestamp,
        receivedTimestamp = receivedTimestamp,
        hourOfDay = hourOfDay,
        summary = summary,
        acknowledged = acknowledged
    )

    private fun SecurityEvent.toEntity() = SecurityEventEntity(
        id = id,
        cameraName = cameraName,
        eventType = eventType.name,
        threatLevel = threatLevel.name,
        confidence = confidence,
        originTimestamp = originTimestamp,
        receivedTimestamp = receivedTimestamp,
        hourOfDay = hourOfDay,
        summary = summary,
        acknowledged = acknowledged
    )
}
