package com.moodtracker.data.database

import androidx.room.*
import com.moodtracker.data.models.NotificationSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_schedules ORDER BY timeOfDay ASC")
    fun getAllSchedules(): Flow<List<NotificationSchedule>>

    @Query("SELECT * FROM notification_schedules WHERE isEnabled = 1 ORDER BY timeOfDay ASC")
    fun getEnabledSchedules(): Flow<List<NotificationSchedule>>

    @Query("SELECT * FROM notification_schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: String): NotificationSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: NotificationSchedule)

    @Update
    suspend fun updateSchedule(schedule: NotificationSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: NotificationSchedule)

    @Query("UPDATE notification_schedules SET isEnabled = :isEnabled WHERE id = :scheduleId")
    suspend fun updateScheduleEnabled(scheduleId: String, isEnabled: Boolean)

    @Query("SELECT COUNT(*) FROM notification_schedules WHERE isEnabled = 1")
    suspend fun getEnabledScheduleCount(): Int
    
    @Query("SELECT COUNT(*) FROM notification_schedules")
    suspend fun getScheduleCount(): Int
}
