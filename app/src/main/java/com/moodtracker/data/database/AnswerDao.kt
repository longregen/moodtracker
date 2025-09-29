package com.moodtracker.data.database

import androidx.room.*
import com.moodtracker.data.models.Answer
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Query("SELECT * FROM answers ORDER BY timestamp DESC")
    fun getAllAnswers(): Flow<List<Answer>>

    @Query("SELECT * FROM answers WHERE questionId = :questionId ORDER BY timestamp DESC")
    fun getAnswersForQuestion(questionId: String): Flow<List<Answer>>

    @Query("SELECT * FROM answers WHERE questionId = :questionId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAnswersForQuestion(questionId: String, limit: Int): Flow<List<Answer>>

    @Query("""
        SELECT a.* FROM answers a
        INNER JOIN (
            SELECT questionId, MAX(timestamp) as maxTimestamp
            FROM answers
            GROUP BY questionId
        ) latest ON a.questionId = latest.questionId AND a.timestamp = latest.maxTimestamp
        ORDER BY a.timestamp DESC
    """)
    fun getLatestAnswerForEachQuestion(): Flow<List<Answer>>

    @Query("SELECT * FROM answers WHERE id = :answerId")
    suspend fun getAnswerById(answerId: String): Answer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: Answer)

    @Update
    suspend fun updateAnswer(answer: Answer)

    @Delete
    suspend fun deleteAnswer(answer: Answer)

    @Query("DELETE FROM answers WHERE questionId = :questionId")
    suspend fun deleteAnswersForQuestion(questionId: String)

    @Query("SELECT COUNT(*) FROM answers WHERE questionId = :questionId")
    suspend fun getAnswerCountForQuestion(questionId: String): Int
}
