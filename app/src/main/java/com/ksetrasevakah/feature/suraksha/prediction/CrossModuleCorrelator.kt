package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.domain.repository.FaultRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.feature.suraksha.domain.model.EventType
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import javax.inject.Inject
import kotlin.math.abs

data class CrossModuleCorrelation(
    val securityEvent: SecurityEvent,
    val pumpIqEventDescription: String,
    val pumpIqTimestamp: Long,
    val timeDeltaMs: Long,
    val correlationType: String,
    val severity: RiskLevel,
    val description: String
)

class CrossModuleCorrelator @Inject constructor(
    private val securityEventRepository: SecurityEventRepository,
    private val telemetryRepository: TelemetryRepository,
    private val faultRepository: FaultRepository
) {

    suspend fun findCrossModuleCorrelations(
        since: Long,
        windowMs: Long = DEFAULT_WINDOW_MS
    ): Result<List<CrossModuleCorrelation>> {
        return try {
            val secEvents = when (val r = securityEventRepository.getRecentEventsSince(since)) {
                is Result.Success -> r.data
                is Result.Error -> return Result.Error(r.message, r.throwable)
                is Result.Loading -> emptyList()
            }
            val telemetry = when (val t = telemetryRepository.getRecentSince(since)) {
                is Result.Success -> t.data
                else -> emptyList()
            }
            val faults = when (val f = faultRepository.getRecentSince(since)) {
                is Result.Success -> f.data
                else -> emptyList()
            }

            if (secEvents.isEmpty()) {
                return Result.Success(emptyList())
            }

            val motorChangeTimes = motorChangeTimestamps(telemetry)
            val correlations = mutableListOf<CrossModuleCorrelation>()

            for (event in secEvents) {
                when (event.eventType) {
                    EventType.PERSON -> {
                        for (t in telemetry.filter { it.suggestsPowerProblem() }) {
                            if (withinWindow(event.originTimestamp, t.timestamp, windowMs)) {
                                val delta = event.originTimestamp - t.timestamp
                                correlations.add(
                                    CrossModuleCorrelation(
                                        securityEvent = event,
                                        pumpIqEventDescription = t.powerProblemLabel(),
                                        pumpIqTimestamp = t.timestamp,
                                        timeDeltaMs = delta,
                                        correlationType = "PERSON_NEAR_POWER_FAILURE",
                                        severity = RiskLevel.HIGH,
                                        description = "Person at ${event.cameraName} — power-related telemetry at pump panel " +
                                            "(${formatDelta(delta)})"
                                    )
                                )
                            }
                        }
                        for (ts in motorChangeTimes) {
                            if (withinWindow(event.originTimestamp, ts, windowMs)) {
                                val delta = event.originTimestamp - ts
                                correlations.add(
                                    CrossModuleCorrelation(
                                        securityEvent = event,
                                        pumpIqEventDescription = "Motor start/stop state change",
                                        pumpIqTimestamp = ts,
                                        timeDeltaMs = delta,
                                        correlationType = "PERSON_NEAR_MOTOR_CHANGE",
                                        severity = RiskLevel.HIGH,
                                        description = "Person at ${event.cameraName} near unexpected motor state change " +
                                            "(${formatDelta(delta)})"
                                    )
                                )
                            }
                        }
                    }
                    EventType.TAMPERING -> {
                        for (f in faults) {
                            if (withinWindow(event.originTimestamp, f.timestamp, windowMs)) {
                                val delta = event.originTimestamp - f.timestamp
                                correlations.add(
                                    CrossModuleCorrelation(
                                        securityEvent = event,
                                        pumpIqEventDescription = "${f.faultType}: ${f.description}",
                                        pumpIqTimestamp = f.timestamp,
                                        timeDeltaMs = delta,
                                        correlationType = "TAMPERING_PLUS_FAULT",
                                        severity = RiskLevel.CRITICAL,
                                        description = "Tampering at ${event.cameraName} with PumpIQ fault logged " +
                                            "(${formatDelta(delta)})"
                                    )
                                )
                            }
                        }
                    }
                    EventType.UNKNOWN -> Unit
                }
            }

            val sorted = correlations
                .distinctBy { c ->
                    "${c.securityEvent.originTimestamp}|${c.securityEvent.cameraName}|" +
                        "${c.correlationType}|${c.pumpIqTimestamp}"
                }
                .sortedWith(
                    compareByDescending<CrossModuleCorrelation> { it.severity.ordinal }
                        .thenByDescending { it.securityEvent.originTimestamp }
                )
            Result.Success(sorted)
        } catch (e: Exception) {
            Result.Error("Cross-module correlation failed: ${e.message}", e)
        }
    }

    private companion object {
        const val DEFAULT_WINDOW_MS = 5 * 60 * 1000L

        fun withinWindow(a: Long, b: Long, windowMs: Long): Boolean = abs(a - b) <= windowMs

        fun formatDelta(deltaMs: Long): String {
            val sec = deltaMs / 1000
            return if (sec == 0L) "same time" else "${sec}s apart"
        }

        fun TelemetryEntity.suggestsPowerProblem(): Boolean {
            val n = narrative?.lowercase().orEmpty()
            return n.contains("power") || n.contains("outage") || n.contains("blackout") ||
                n.contains("voltage") || (voltage != null && voltage < 180f)
        }

        fun TelemetryEntity.powerProblemLabel(): String =
            narrative?.takeIf { it.isNotBlank() }
                ?: "Voltage ${voltage ?: "?"} V — possible supply issue"

        fun motorChangeTimestamps(telemetry: List<TelemetryEntity>): List<Long> {
            val sorted = telemetry.sortedBy { it.timestamp }
            if (sorted.size < 2) return emptyList()
            val out = ArrayList<Long>()
            for (i in 1 until sorted.size) {
                if (sorted[i].motorOn != sorted[i - 1].motorOn) {
                    out.add(sorted[i].timestamp)
                }
            }
            return out
        }
    }
}
