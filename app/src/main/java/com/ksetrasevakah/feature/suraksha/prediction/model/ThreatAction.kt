package com.ksetrasevakah.feature.suraksha.prediction.model

import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel

sealed class ThreatAction {

    data class LogOnly(
        val threatLevel: ThreatLevel,
        val confidence: Float,
        val summary: String
    ) : ThreatAction()

    data class Notify(
        val threatLevel: ThreatLevel,
        val confidence: Float,
        val summary: String,
        val title: String
    ) : ThreatAction()

    data class CriticalAlarm(
        val threatLevel: ThreatLevel,
        val confidence: Float,
        val summary: String,
        val title: String,
        val cameraName: String
    ) : ThreatAction()

    data class Drop(val reason: String) : ThreatAction()
}
