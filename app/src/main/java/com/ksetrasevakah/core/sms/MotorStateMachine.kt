package com.ksetrasevakah.core.sms

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.database.dao.WorkerActivityDao
import com.ksetrasevakah.core.database.entity.MotorStateEntity
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import com.ksetrasevakah.designsystem.model.MotorState
import java.time.LocalDate
import javax.inject.Inject

class MotorStateMachine @Inject constructor(
    private val motorStateDao: MotorStateDao,
    private val workerActivityDao: WorkerActivityDao
) {

    suspend fun sendStart(): Result<MotorState> {
        val current = getCurrentState()
        return when (current) {
            MotorState.OFF -> {
                updateMotorState(MotorState.PENDING_START, pendingCommand = "START")
                Result.Success(MotorState.PENDING_START)
            }
            MotorState.ON ->
                Result.Error("Motor is already running")
            MotorState.PENDING_START, MotorState.PENDING_STOP ->
                Result.Error("Command already in progress")
        }
    }

    suspend fun sendStop(): Result<MotorState> {
        val current = getCurrentState()
        return when (current) {
            MotorState.ON -> {
                updateMotorState(MotorState.PENDING_STOP, pendingCommand = "STOP")
                Result.Success(MotorState.PENDING_STOP)
            }
            MotorState.OFF ->
                Result.Error("Motor is already off")
            MotorState.PENDING_START, MotorState.PENDING_STOP ->
                Result.Error("Command already in progress")
        }
    }

    suspend fun processConfirmation(smsBody: String): MotorState {
        val body = smsBody.trim().uppercase()
        return when {
            body.startsWith("MOTOR ON") -> {
                val now = System.currentTimeMillis()
                motorStateDao.setSessionStart(now)
                logWorkerOn(now)
                updateMotorState(MotorState.ON)
                MotorState.ON
            }
            body.startsWith("MOTOR OFF") -> {
                val now = System.currentTimeMillis()
                motorStateDao.setSessionEnd(now)
                logWorkerOff(now)
                updateMotorState(MotorState.OFF)
                MotorState.OFF
            }
            else -> getCurrentState()
        }
    }

    suspend fun handleTimeout() {
        val entity = motorStateDao.get() ?: return
        if (entity.pendingSince == null) return

        val elapsed = System.currentTimeMillis() - entity.pendingSince
        if (elapsed >= TIMEOUT_MS) {
            val fallbackState = when (entity.state) {
                MotorState.PENDING_START.name -> MotorState.OFF
                MotorState.PENDING_STOP.name -> MotorState.ON
                else -> return
            }
            updateMotorState(fallbackState)
        }
    }

    suspend fun getCurrentState(): MotorState {
        val entity = motorStateDao.get() ?: return MotorState.OFF
        return try {
            MotorState.valueOf(entity.state)
        } catch (_: IllegalArgumentException) {
            MotorState.OFF
        }
    }

    private suspend fun updateMotorState(
        state: MotorState,
        pendingCommand: String? = null
    ) {
        val now = System.currentTimeMillis()
        val existing = motorStateDao.get()
        if (existing == null) {
            motorStateDao.upsert(
                MotorStateEntity(
                    state = state.name,
                    pendingCommand = pendingCommand,
                    pendingSince = if (pendingCommand != null) now else null,
                    updatedAt = now
                )
            )
        } else {
            motorStateDao.updateState(state.name, now)
            motorStateDao.setPendingCommand(
                pendingCommand,
                if (pendingCommand != null) now else null,
                now
            )
        }
    }

    private suspend fun logWorkerOn(timestamp: Long) {
        val date = LocalDate.now().toString()
        val existing = workerActivityDao.getByDate(date)
        if (existing == null) {
            workerActivityDao.upsert(
                WorkerActivityEntity(date = date, onTime = timestamp)
            )
        } else {
            workerActivityDao.upsert(existing.copy(onTime = timestamp))
        }
    }

    private suspend fun logWorkerOff(timestamp: Long) {
        val date = LocalDate.now().toString()
        val existing = workerActivityDao.getByDate(date)
        if (existing != null) {
            val durationMin = existing.onTime?.let {
                ((timestamp - it) / 60_000).toInt()
            }
            workerActivityDao.upsert(
                existing.copy(offTime = timestamp, durationMinutes = durationMin)
            )
        }
    }

    companion object {
        private const val TIMEOUT_MS = 30_000L
    }
}
