package com.ksetrasevakah.feature.pumpiq.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.FaultDao
import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.database.entity.FaultEntity
import com.ksetrasevakah.designsystem.model.MotorState
import javax.inject.Inject

class ProcessSmsConfirmationUseCase @Inject constructor(
    private val motorStateDao: MotorStateDao,
    private val faultDao: FaultDao
) {

    suspend operator fun invoke(smsBody: String): Result<MotorState> {
        val body = smsBody.trim().uppercase()
        val now = System.currentTimeMillis()

        return when {
            body.startsWith("MOTOR ON") -> {
                motorStateDao.updateState(MotorState.ON.name, now)
                motorStateDao.setPendingCommand(null, null, now)
                motorStateDao.setSessionStart(now)
                Result.Success(MotorState.ON)
            }
            body.startsWith("MOTOR OFF") -> {
                motorStateDao.updateState(MotorState.OFF.name, now)
                motorStateDao.setPendingCommand(null, null, now)
                motorStateDao.setSessionEnd(now)
                Result.Success(MotorState.OFF)
            }
            body.startsWith("ALERT:") -> {
                val description = smsBody.trim().removePrefix("ALERT:").trim()
                faultDao.insert(
                    FaultEntity(
                        timestamp = now,
                        faultType = "SMS_ALERT",
                        description = description
                    )
                )
                motorStateDao.updateState(MotorState.OFF.name, now)
                motorStateDao.setPendingCommand(null, null, now)
                motorStateDao.setSessionEnd(now)
                Result.Success(MotorState.OFF)
            }
            else -> Result.Error("Unrecognised SMS confirmation: $smsBody")
        }
    }
}
