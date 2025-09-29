package com.moodtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_schedules")
data class NotificationSchedule(
    @PrimaryKey
    val id: String,
    val timeOfDay: String, // HH:mm format (24-hour)
    val isEnabled: Boolean = true
)
