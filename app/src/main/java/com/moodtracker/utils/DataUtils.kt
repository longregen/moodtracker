package com.moodtracker.utils

import com.moodtracker.data.models.Question
import com.moodtracker.data.models.QuestionType
import com.moodtracker.data.models.NotificationSchedule
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

object DataUtils {
    
    fun generateId(): String = UUID.randomUUID().toString()
    
    fun getCurrentInstant(): Instant = Clock.System.now()
    
    fun createDefaultQuestions(): List<Question> {
        val now = getCurrentInstant()
        return listOf(
            Question(
                id = generateId(),
                text = "How is your mood?",
                type = QuestionType.MULTIPLE_CHOICE,
                options = listOf("Great", "Good", "Okay", "Not great", "Terrible"),
                createdAt = now,
                modifiedAt = now
            ),
            Question(
                id = generateId(),
                text = "Have you exercised today yet?",
                type = QuestionType.YES_NO,
                createdAt = now,
                modifiedAt = now
            ),
            Question(
                id = generateId(),
                text = "How many hours were you sitting at the computer?",
                type = QuestionType.NUMBER,
                createdAt = now,
                modifiedAt = now
            ),
            Question(
                id = generateId(),
                text = "Any reminders you would like for the future?",
                type = QuestionType.TEXT,
                createdAt = now,
                modifiedAt = now
            )
        )
    }
    
    fun createDefaultNotificationSchedules(): List<NotificationSchedule> {
        return listOf(
            NotificationSchedule(
                id = generateId(),
                timeOfDay = "09:00"
            ),
            NotificationSchedule(
                id = generateId(),
                timeOfDay = "13:00"
            ),
            NotificationSchedule(
                id = generateId(),
                timeOfDay = "17:00"
            ),
            NotificationSchedule(
                id = generateId(),
                timeOfDay = "21:00"
            )
        )
    }
    
    fun formatTimeOfDay(timeOfDay: String): String {
        val parts = timeOfDay.split(":")
        if (parts.size != 2) return timeOfDay
        
        val hour = parts[0].toIntOrNull() ?: return timeOfDay
        val minute = parts[1].toIntOrNull() ?: return timeOfDay
        
        val period = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        
        return String.format("%d:%02d %s", displayHour, minute, period)
    }
    
    fun validateTimeOfDay(timeOfDay: String): Boolean {
        val parts = timeOfDay.split(":")
        if (parts.size != 2) return false
        
        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false
        
        return hour in 0..23 && minute in 0..59
    }
}
