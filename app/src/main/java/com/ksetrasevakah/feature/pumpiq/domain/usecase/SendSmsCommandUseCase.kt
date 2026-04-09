package com.ksetrasevakah.feature.pumpiq.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.sms.SmsCommandSender
import com.ksetrasevakah.core.sms.model.SmsCommand
import com.ksetrasevakah.designsystem.model.MotorState
import javax.inject.Inject

class SendSmsCommandUseCase @Inject constructor(
    private val sender: SmsCommandSender,
    private val motorStateDao: MotorStateDao
) {

    suspend operator fun invoke(command: SmsCommand): Result<Unit> {
        val entity = motorStateDao.get()
        val currentState = entity?.let {
            try { MotorState.valueOf(it.state) } catch (_: IllegalArgumentException) { MotorState.OFF }
        } ?: MotorState.OFF

        if (currentState.isPending) {
            return Result.Error("Command already in progress")
        }

        when (command) {
            is SmsCommand.Start -> {
                if (currentState == MotorState.ON) {
                    return Result.Error("Motor is already running")
                }
            }
            is SmsCommand.Stop -> {
                if (currentState == MotorState.OFF) {
                    return Result.Error("Motor is already off")
                }
            }
            is SmsCommand.Status -> { /* always allowed */ }
        }

        val sendResult = sender.sendCommand(command)
        if (sendResult.isFailure) {
            return Result.Error(
                sendResult.exceptionOrNull()?.message ?: "Failed to send SMS"
            )
        }

        val now = System.currentTimeMillis()
        when (command) {
            is SmsCommand.Start -> {
                motorStateDao.updateState(MotorState.PENDING_START.name, now)
                motorStateDao.setPendingCommand("START", now, now)
            }
            is SmsCommand.Stop -> {
                motorStateDao.updateState(MotorState.PENDING_STOP.name, now)
                motorStateDao.setPendingCommand("STOP", now, now)
            }
            is SmsCommand.Status -> { /* no state change */ }
        }

        return Result.Success(Unit)
    }
}
