package com.moodtracker.data.repository

import com.moodtracker.data.database.AnswerDao
import com.moodtracker.data.database.NotificationDao
import com.moodtracker.data.database.QuestionDao
import com.moodtracker.data.models.Answer
import com.moodtracker.data.models.NotificationSchedule
import com.moodtracker.data.models.Question
import kotlinx.coroutines.flow.Flow

class MoodTrackerRepository(
    private val questionDao: QuestionDao,
    private val answerDao: AnswerDao,
    private val notificationDao: NotificationDao
) {
    private var isInitialized = false
    
    suspend fun initializeDefaultDataIfNeeded() {
        if (isInitialized) return
        
        val questionCount = questionDao.getQuestionCount()
        val scheduleCount = notificationDao.getScheduleCount()
        
        if (questionCount == 0) {
            val defaultQuestions = com.moodtracker.utils.DataUtils.createDefaultQuestions()
            defaultQuestions.forEach { question ->
                insertQuestion(question)
            }
        }
        
        if (scheduleCount == 0) {
            val defaultSchedules = com.moodtracker.utils.DataUtils.createDefaultNotificationSchedules()
            defaultSchedules.forEach { schedule ->
                insertSchedule(schedule)
            }
        }
        
        isInitialized = true
    }
    // Question operations
    fun getAllActiveQuestions(): Flow<List<Question>> = questionDao.getAllActiveQuestions()
    fun getAllQuestions(): Flow<List<Question>> = questionDao.getAllQuestions()
    suspend fun getQuestionById(questionId: String): Question? = questionDao.getQuestionById(questionId)
    suspend fun insertQuestion(question: Question) = questionDao.insertQuestion(question)
    suspend fun updateQuestion(question: Question) = questionDao.updateQuestion(question)
    suspend fun deleteQuestion(question: Question) = questionDao.deleteQuestion(question)
    suspend fun updateQuestionVisibility(questionId: String, isHidden: Boolean) = 
        questionDao.updateQuestionVisibility(questionId, isHidden)
    suspend fun getActiveQuestionCount(): Int = questionDao.getActiveQuestionCount()

    // Answer operations
    fun getAllAnswers(): Flow<List<Answer>> = answerDao.getAllAnswers()
    fun getAnswersForQuestion(questionId: String): Flow<List<Answer>> = 
        answerDao.getAnswersForQuestion(questionId)
    fun getRecentAnswersForQuestion(questionId: String, limit: Int): Flow<List<Answer>> = 
        answerDao.getRecentAnswersForQuestion(questionId, limit)
    fun getLatestAnswerForEachQuestion(): Flow<List<Answer>> = 
        answerDao.getLatestAnswerForEachQuestion()
    suspend fun getAnswerById(answerId: String): Answer? = answerDao.getAnswerById(answerId)
    suspend fun insertAnswer(answer: Answer) = answerDao.insertAnswer(answer)
    suspend fun updateAnswer(answer: Answer) = answerDao.updateAnswer(answer)
    suspend fun deleteAnswer(answer: Answer) = answerDao.deleteAnswer(answer)
    suspend fun deleteAnswersForQuestion(questionId: String) = 
        answerDao.deleteAnswersForQuestion(questionId)
    suspend fun getAnswerCountForQuestion(questionId: String): Int = 
        answerDao.getAnswerCountForQuestion(questionId)

    // Notification schedule operations
    fun getAllSchedules(): Flow<List<NotificationSchedule>> = notificationDao.getAllSchedules()
    fun getEnabledSchedules(): Flow<List<NotificationSchedule>> = notificationDao.getEnabledSchedules()
    suspend fun getScheduleById(scheduleId: String): NotificationSchedule? = 
        notificationDao.getScheduleById(scheduleId)
    suspend fun insertSchedule(schedule: NotificationSchedule) = notificationDao.insertSchedule(schedule)
    suspend fun updateSchedule(schedule: NotificationSchedule) = notificationDao.updateSchedule(schedule)
    suspend fun deleteSchedule(schedule: NotificationSchedule) = notificationDao.deleteSchedule(schedule)
    suspend fun updateScheduleEnabled(scheduleId: String, isEnabled: Boolean) = 
        notificationDao.updateScheduleEnabled(scheduleId, isEnabled)
    suspend fun getEnabledScheduleCount(): Int = notificationDao.getEnabledScheduleCount()
}
