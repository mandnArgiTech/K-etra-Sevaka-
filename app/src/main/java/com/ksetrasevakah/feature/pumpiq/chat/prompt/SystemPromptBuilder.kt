package com.ksetrasevakah.feature.pumpiq.chat.prompt

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.SecurityBriefingDao
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.PredictionRepository
import com.ksetrasevakah.core.vectorstore.RagPipeline
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import com.ksetrasevakah.feature.suraksha.prediction.CrossModuleCorrelator
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SystemPromptBuilder @Inject constructor(
    private val motorStateRepository: MotorStateRepository,
    private val predictionRepository: PredictionRepository,
    private val ragPipeline: RagPipeline,
    private val cameraConfigRepository: CameraConfigRepository,
    private val securityEventRepository: SecurityEventRepository,
    private val securityBriefingDao: SecurityBriefingDao,
    private val crossModuleCorrelator: CrossModuleCorrelator
) {

    suspend fun build(ragQuery: String = ""): String {
        val motorState = try {
            val result = motorStateRepository.observeMotorState().first()
            if (result is Result.Success) result.data.displayLabel else "UNKNOWN"
        } catch (_: Exception) {
            "UNKNOWN"
        }

        val prediction = when (val result = predictionRepository.getByType("failure")) {
            is Result.Success -> result.data?.let {
                "Prediction: ${it.type} - confidence ${it.confidence}"
            } ?: "No predictions available"
            else -> "Predictions unavailable"
        }

        val ragContext = if (ragQuery.isNotBlank()) {
            when (
                val result = ragPipeline.query(
                    queryText = ragQuery,
                    topK = Constants.VECTOR_SEARCH_TOP_K,
                    namespaces = setOf(Constants.RAG_MODULE_PUMPIQ, Constants.RAG_MODULE_SURAKSHA)
                )
            ) {
                is Result.Success -> result.data
                else -> ""
            }
        } else {
            ""
        }

        val surakshaSection = buildSurakshaSection()

        val body = buildString {
            appendLine("You are PumpIQ, an AI assistant for agricultural motor pump management.")
            appendLine("You help farmers monitor, maintain, and optimize their water pump systems.")
            appendLine()
            appendLine("Current Motor State: $motorState")
            appendLine(prediction)
            if (ragContext.isNotBlank()) {
                appendLine()
                appendLine("Relevant context from knowledge base:")
                appendLine(ragContext)
            }
            appendLine()
            appendLine(surakshaSection)
            appendLine()
            appendLine("Provide concise, actionable advice. Use simple language suitable for farmers.")
            appendLine("If data is insufficient, say so rather than guessing.")
        }

        return body.take(Constants.SYSTEM_PROMPT_MAX_CHARS)
    }

    private suspend fun buildSurakshaSection(): String {
        val since24h = System.currentTimeMillis() - 86_400_000L
        return buildString {
            appendLine("[MODULE: Surakṣā]")

            val cameraResult = try {
                cameraConfigRepository.observeAllCameras().first()
            } catch (_: Exception) {
                null
            }
            val activeCameras = (cameraResult as? Result.Success)?.data?.size ?: 0
            appendLine("Active cameras (configured): $activeCameras")

            val dist = securityEventRepository.getThreatDistribution(since24h)
            val latest = securityEventRepository.getLatestEvent()

            when (dist) {
                is Result.Error -> {
                    appendLine("Threat summaries unavailable.")
                    when (latest) {
                        is Result.Success -> {
                            latest.data?.let { ev ->
                                appendLine(latestEventLine(ev))
                            }
                        }
                        is Result.Error -> appendLine("Latest event: unavailable.")
                        else -> Unit
                    }
                }
                is Result.Success -> {
                    val distRows = dist.data
                    val totalEvents = distRows.sumOf { it.count }
                    val latestData = (latest as? Result.Success)?.data
                    if (totalEvents == 0 && latestData == null) {
                        appendLine("No security events recorded.")
                    } else {
                        appendLine("Last 24h event counts by threat level:")
                        if (distRows.isEmpty()) {
                            appendLine("(no rows in window)")
                        } else {
                            distRows.forEach { row ->
                                appendLine("- ${row.threatLevel}: ${row.count}")
                            }
                        }
                        when (latest) {
                            is Result.Success ->
                                latest.data?.let { appendLine(latestEventLine(it)) }
                            is Result.Error ->
                                appendLine("Latest event: unavailable.")
                            else -> Unit
                        }
                    }
                }
                is Result.Loading -> {
                    appendLine("Threat summaries loading.")
                    when (latest) {
                        is Result.Success ->
                            latest.data?.let { appendLine(latestEventLine(it)) }
                        is Result.Error ->
                            appendLine("Latest event: unavailable.")
                        else -> Unit
                    }
                }
            }

            val briefing = try {
                securityBriefingDao.getRecent(1).firstOrNull()
            } catch (_: Exception) {
                null
            }
            if (briefing != null) {
                val summary = briefing.summary.take(BRIEFING_MAX_CHARS)
                appendLine("Latest security briefing summary: $summary")
            }

            when (val corr = crossModuleCorrelator.findCrossModuleCorrelations(since24h)) {
                is Result.Success -> {
                    if (corr.data.isNotEmpty()) {
                        appendLine()
                        appendLine("CROSS-MODULE CORRELATIONS:")
                        corr.data.take(CORRELATION_LINES_MAX).forEach { c ->
                            val line = "- ${c.description.take(CORRELATION_DESC_MAX)} [${c.correlationType}]"
                            appendLine(line)
                        }
                    }
                }
                is Result.Error -> appendLine("Cross-module correlations unavailable.")
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    private fun latestEventLine(latestEv: SecurityEvent): String {
        val desc = (latestEv.summary ?: latestEv.description).take(LATEST_EVENT_DESC_MAX)
        return "Latest event: ${latestEv.eventType} at ${latestEv.cameraName} — ${latestEv.threatLevel}: $desc"
    }

    private companion object {
        const val BRIEFING_MAX_CHARS = 400
        const val CORRELATION_LINES_MAX = 12
        const val CORRELATION_DESC_MAX = 180
        const val LATEST_EVENT_DESC_MAX = 200
    }
}
