package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.ai.prompt.SecurityBriefingPrompt
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import kotlinx.coroutines.flow.fold
import javax.inject.Inject

class SecurityBriefingGenerator @Inject constructor(
    private val engine: MlcLlmEngine,
    private val securityEventRepository: SecurityEventRepository
) {

    suspend fun generate(): Result<String> {
        return try {
            val eventsResult = securityEventRepository.getEventsInWindow(
                windowMs = 24 * 60 * 60 * 1000L
            )
            val events = when (eventsResult) {
                is Result.Success -> eventsResult.data
                is Result.Error -> return Result.Error(eventsResult.message, eventsResult.throwable)
                is Result.Loading -> return Result.Loading
            }

            val countsResult = securityEventRepository.getThreatCounts()
            val counts = when (countsResult) {
                is Result.Success -> countsResult.data
                is Result.Error -> return Result.Error(countsResult.message, countsResult.throwable)
                is Result.Loading -> return Result.Loading
            }

            val prompt = SecurityBriefingPrompt.getBriefingPrompt(events, counts)
            val briefing = engine.generate(prompt, Constants.INGESTION_MODEL_ID)
                .fold(StringBuilder()) { acc, token -> acc.append(token) }
                .toString()
                .trim()

            if (briefing.isBlank()) {
                Result.Error("Model returned empty briefing")
            } else {
                Result.Success(briefing)
            }
        } catch (e: Exception) {
            Result.Error("Briefing generation failed: ${e.message}", e)
        }
    }
}
