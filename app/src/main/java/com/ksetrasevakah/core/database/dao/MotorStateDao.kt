package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.MotorStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MotorStateDao {
    @Query("SELECT * FROM motor_state WHERE id = 1")
    fun observe(): Flow<MotorStateEntity?>

    @Query("SELECT * FROM motor_state WHERE id = 1")
    suspend fun get(): MotorStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MotorStateEntity)

    @Query("UPDATE motor_state SET state = :state, updated_at = :now WHERE id = 1")
    suspend fun updateState(state: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE motor_state SET pending_command = :command, pending_since = :since, updated_at = :now WHERE id = 1")
    suspend fun setPendingCommand(command: String?, since: Long?, now: Long = System.currentTimeMillis())

    @Query("UPDATE motor_state SET current_session_start = :start, last_on_time = :start, updated_at = :now WHERE id = 1")
    suspend fun setSessionStart(start: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE motor_state SET current_session_start = null, last_off_time = :offTime, updated_at = :now WHERE id = 1")
    suspend fun setSessionEnd(offTime: Long, now: Long = System.currentTimeMillis())
}
