package com.moodtracker.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moodtracker.R
import kotlinx.datetime.*

object UIUtils {
    
    @Composable
    fun formatAnswerTime(timestamp: Instant): String {
        val now = Clock.System.now()
        val answerTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
        val nowTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        
        return when {
            answerTime.date == nowTime.date -> {
                stringResource(
                    R.string.time_format_today, 
                    "${answerTime.hour}:${answerTime.minute.toString().padStart(2, '0')}"
                )
            }
            answerTime.date == nowTime.date.minus(1, DateTimeUnit.DAY) -> {
                stringResource(
                    R.string.time_format_yesterday, 
                    "${answerTime.hour}:${answerTime.minute.toString().padStart(2, '0')}"
                )
            }
            else -> {
                stringResource(
                    R.string.time_format_date,
                    "${answerTime.monthNumber}/${answerTime.dayOfMonth}",
                    "${answerTime.hour}:${answerTime.minute.toString().padStart(2, '0')}"
                )
            }
        }
    }
    
    fun calculateTimeUntilNext(nextTime: String): Pair<String, String>? {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentTime = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
        
        val timeParts = nextTime.split(":")
        if (timeParts.size != 2) return null
        
        val nextHour = timeParts[0].toIntOrNull() ?: return null
        val nextMinute = timeParts[1].toIntOrNull() ?: return null
        
        var nextDateTime = now.date.atTime(nextHour, nextMinute)
        if (nextTime <= currentTime) {
            // Next notification is tomorrow
            nextDateTime = nextDateTime.date.plus(1, DateTimeUnit.DAY).atTime(nextHour, nextMinute)
        }
        
        val duration = nextDateTime.toInstant(TimeZone.currentSystemDefault()) - 
                      now.toInstant(TimeZone.currentSystemDefault())
        val hours = duration.inWholeHours
        val minutes = (duration.inWholeMinutes % 60)
        
        val formattedTime = DataUtils.formatTimeOfDay(nextTime)
        val timeUntil = when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "Less than 1 minute"
        }
        
        return Pair(formattedTime, timeUntil)
    }
    
    fun formatDateOnly(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.monthNumber}/${localDateTime.dayOfMonth}/${localDateTime.year}"
    }
    
    fun formatTimeOnly(timestamp: Instant): String {
        val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }
    
    fun getDayHeader(timestamp: Instant): String {
        val now = Clock.System.now()
        val answerDate = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nowDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        return when {
            answerDate == nowDate -> "Today"
            answerDate == nowDate.minus(1, DateTimeUnit.DAY) -> "Yesterday"
            else -> {
                val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                "${monthNames[localDateTime.monthNumber - 1]} ${localDateTime.dayOfMonth}, ${localDateTime.year}"
            }
        }
    }
    
    fun isSameDay(timestamp1: Instant, timestamp2: Instant): Boolean {
        val date1 = timestamp1.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val date2 = timestamp2.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return date1 == date2
    }
}