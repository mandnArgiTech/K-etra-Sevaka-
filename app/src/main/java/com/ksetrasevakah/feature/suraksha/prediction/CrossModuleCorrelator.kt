package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import javax.inject.Inject

data class TemporalCorrelation(
    val securityEvents: List<SecurityEvent>,
    val windowMs: Long,
    val correlationScore: Float
)

class CrossModuleCorrelator @Inject constructor(
    private val securityEventRepository: SecurityEventRepository
) {

    suspend fun findTemporalCorrelations(
        windowMs: Long = Constants.CROSS_MODULE_CORRELATION_WINDOW_MS
    ): Result<List<TemporalCorrelation>> {
        return try {
            val eventsResult = securityEventRepository.getEventsInWindow(windowMs)
            when (eventsResult) {
                is Result.Success -> {
                    val correlations = correlate(eventsResult.data, windowMs)
                    Result.Success(correlations)
                }
                is Result.Error -> Result.Error(eventsResult.message, eventsResult.throwable)
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error("Correlation failed: ${e.message}", e)
        }
    }

    private fun correlate(
        events: List<SecurityEvent>,
        windowMs: Long
    ): List<TemporalCorrelation> {
        if (events.size < 2) return emptyList()

        val sorted = events.sortedBy { it.timestamp }
        val correlations = mutableListOf<TemporalCorrelation>()
        var i = 0

        while (i < sorted.size) {
            val cluster = mutableListOf(sorted[i])
            var j = i + 1
            while (j < sorted.size && sorted[j].timestamp - sorted[i].timestamp <= windowMs) {
                cluster.add(sorted[j])
                j++
            }

            if (cluster.size >= 2) {
                val score = computeCorrelationScore(cluster, windowMs)
                correlations.add(
                    TemporalCorrelation(
                        securityEvents = cluster,
                        windowMs = windowMs,
                        correlationScore = score
                    )
                )
            }
            i = j
        }

        return correlations
    }

    private fun computeCorrelationScore(events: List<SecurityEvent>, windowMs: Long): Float {
        val uniqueTypes = events.map { it.eventType }.distinct().size
        val densityFactor = events.size.toFloat() / (windowMs / 60_000f)
        val diversityFactor = uniqueTypes.toFloat() / events.size
        return (densityFactor * 0.6f + diversityFactor * 0.4f).coerceIn(0f, 1f)
    }
}
