package com.moodtracker.data.database

import androidx.room.*
import com.moodtracker.data.models.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE isHidden = 0 ORDER BY createdAt ASC")
    fun getAllActiveQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions ORDER BY createdAt ASC")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionById(questionId: String): Question?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @Query("UPDATE questions SET isHidden = :isHidden WHERE id = :questionId")
    suspend fun updateQuestionVisibility(questionId: String, isHidden: Boolean)

    @Query("SELECT COUNT(*) FROM questions WHERE isHidden = 0")
    suspend fun getActiveQuestionCount(): Int
    
    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int
}
