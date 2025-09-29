package com.moodtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.Instant

@Entity(tableName = "questions")
@TypeConverters(QuestionConverters::class)
data class Question(
    @PrimaryKey
    val id: String,
    val text: String,
    val type: QuestionType,
    val options: List<String>? = null, // For multiple choice questions
    val isHidden: Boolean = false,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val version: Int = 1
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    YES_NO,
    NUMBER,
    TEXT
}

class QuestionConverters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString("|")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split("|")?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromQuestionType(value: QuestionType): String {
        return value.name
    }

    @TypeConverter
    fun toQuestionType(value: String): QuestionType {
        return QuestionType.valueOf(value)
    }

    @TypeConverter
    fun fromInstant(value: Instant): Long {
        return value.toEpochMilliseconds()
    }

    @TypeConverter
    fun toInstant(value: Long): Instant {
        return Instant.fromEpochMilliseconds(value)
    }
}
