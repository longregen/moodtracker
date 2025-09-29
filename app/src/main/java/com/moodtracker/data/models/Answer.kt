package com.moodtracker.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Instant

@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["questionId"]), Index(value = ["timestamp"])]
)
@TypeConverters(QuestionConverters::class)
data class Answer(
    @PrimaryKey
    val id: String,
    val questionId: String,
    val questionVersion: Int,
    val answerText: String,
    val additionalNotes: String? = null,
    val timestamp: Instant,
    val wasSnooze: Boolean = false
)
