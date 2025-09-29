package com.moodtracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.moodtracker.data.models.Answer
import com.moodtracker.data.models.Question
import com.moodtracker.data.models.QuestionConverters
import com.moodtracker.data.models.NotificationSchedule

@Database(
    entities = [Question::class, Answer::class, NotificationSchedule::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(QuestionConverters::class)
abstract class MoodTrackerDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: MoodTrackerDatabase? = null

        fun getDatabase(context: Context): MoodTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodTrackerDatabase::class.java,
                    "mood_tracker_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
