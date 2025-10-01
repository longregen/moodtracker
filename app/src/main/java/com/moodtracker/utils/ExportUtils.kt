package com.moodtracker.utils

import com.moodtracker.data.models.Answer
import com.moodtracker.data.models.Question
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object ExportUtils {
    
    fun exportToCSV(
        answers: List<Answer>,
        questions: List<Question>
    ): String {
        val csv = StringBuilder()
        
        // Add CSV header
        csv.append("Date,Time,Question,Answer,Notes,Snoozed\n")
        
        // Add data rows
        answers.forEach { answer ->
            val question = questions.find { it.id == answer.questionId }
            val localDateTime = answer.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
            
            val date = "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
            val time = "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
            val questionText = escapeCSV(question?.text ?: "Unknown Question")
            val answerText = escapeCSV(answer.answerText)
            val notes = escapeCSV(answer.additionalNotes ?: "")
            val snoozed = if (answer.wasSnooze) "Yes" else "No"
            
            csv.append("$date,$time,$questionText,$answerText,$notes,$snoozed\n")
        }
        
        return csv.toString()
    }
    
    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}