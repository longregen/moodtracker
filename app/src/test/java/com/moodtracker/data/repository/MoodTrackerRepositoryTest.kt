package com.moodtracker.data.repository

import com.moodtracker.data.database.AnswerDao
import com.moodtracker.data.database.NotificationDao
import com.moodtracker.data.database.QuestionDao
import com.moodtracker.data.models.Question
import com.moodtracker.data.models.QuestionType
import com.moodtracker.data.models.NotificationSchedule
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class MoodTrackerRepositoryTest {

    @Mock
    private lateinit var questionDao: QuestionDao

    @Mock
    private lateinit var answerDao: AnswerDao

    @Mock
    private lateinit var notificationDao: NotificationDao

    private lateinit var repository: MoodTrackerRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = MoodTrackerRepository(questionDao, answerDao, notificationDao)
    }

    @Test
    fun `initializeDefaultDataIfNeeded creates default questions when database is empty`() = runTest {
        // Given
        whenever(questionDao.getQuestionCount()).thenReturn(0)
        whenever(notificationDao.getScheduleCount()).thenReturn(0)

        // When
        repository.initializeDefaultDataIfNeeded()

        // Then
        verify(questionDao).getQuestionCount()
        verify(notificationDao).getScheduleCount()
        verify(questionDao, times(4)).insertQuestion(any()) // 4 default questions
        verify(notificationDao, times(4)).insertSchedule(any()) // 4 default schedules
    }

    @Test
    fun `initializeDefaultDataIfNeeded does not create questions when they already exist`() = runTest {
        // Given
        whenever(questionDao.getQuestionCount()).thenReturn(5)
        whenever(notificationDao.getScheduleCount()).thenReturn(3)

        // When
        repository.initializeDefaultDataIfNeeded()

        // Then
        verify(questionDao).getQuestionCount()
        verify(notificationDao).getScheduleCount()
        verify(questionDao, never()).insertQuestion(any())
        verify(notificationDao, never()).insertSchedule(any())
    }

    @Test
    fun `initializeDefaultDataIfNeeded creates only questions when schedules exist`() = runTest {
        // Given
        whenever(questionDao.getQuestionCount()).thenReturn(0)
        whenever(notificationDao.getScheduleCount()).thenReturn(3)

        // When
        repository.initializeDefaultDataIfNeeded()

        // Then
        verify(questionDao).getQuestionCount()
        verify(notificationDao).getScheduleCount()
        verify(questionDao, times(4)).insertQuestion(any())
        verify(notificationDao, never()).insertSchedule(any())
    }

    @Test
    fun `initializeDefaultDataIfNeeded creates only schedules when questions exist`() = runTest {
        // Given
        whenever(questionDao.getQuestionCount()).thenReturn(5)
        whenever(notificationDao.getScheduleCount()).thenReturn(0)

        // When
        repository.initializeDefaultDataIfNeeded()

        // Then
        verify(questionDao).getQuestionCount()
        verify(notificationDao).getScheduleCount()
        verify(questionDao, never()).insertQuestion(any())
        verify(notificationDao, times(4)).insertSchedule(any())
    }

    @Test
    fun `initializeDefaultDataIfNeeded is called only once even with multiple calls`() = runTest {
        // Given
        whenever(questionDao.getQuestionCount()).thenReturn(0)
        whenever(notificationDao.getScheduleCount()).thenReturn(0)

        // When
        repository.initializeDefaultDataIfNeeded()
        repository.initializeDefaultDataIfNeeded()
        repository.initializeDefaultDataIfNeeded()

        // Then
        verify(questionDao, times(1)).getQuestionCount()
        verify(notificationDao, times(1)).getScheduleCount()
        verify(questionDao, times(4)).insertQuestion(any()) // Only called once
        verify(notificationDao, times(4)).insertSchedule(any()) // Only called once
    }

    @Test
    fun `insertQuestion calls questionDao insertQuestion`() = runTest {
        // Given
        val question = Question(
            id = "test-id",
            text = "Test question",
            type = QuestionType.TEXT,
            createdAt = Clock.System.now(),
            modifiedAt = Clock.System.now()
        )

        // When
        repository.insertQuestion(question)

        // Then
        verify(questionDao).insertQuestion(question)
    }

    @Test
    fun `updateQuestion calls questionDao updateQuestion`() = runTest {
        // Given
        val question = Question(
            id = "test-id",
            text = "Updated question",
            type = QuestionType.TEXT,
            createdAt = Clock.System.now(),
            modifiedAt = Clock.System.now(),
            version = 2
        )

        // When
        repository.updateQuestion(question)

        // Then
        verify(questionDao).updateQuestion(question)
    }

    @Test
    fun `deleteQuestion calls questionDao deleteQuestion`() = runTest {
        // Given
        val question = Question(
            id = "test-id",
            text = "Test question",
            type = QuestionType.TEXT,
            createdAt = Clock.System.now(),
            modifiedAt = Clock.System.now()
        )

        // When
        repository.deleteQuestion(question)

        // Then
        verify(questionDao).deleteQuestion(question)
    }

    @Test
    fun `updateQuestionVisibility calls questionDao updateQuestionVisibility`() = runTest {
        // Given
        val questionId = "test-id"
        val isHidden = true

        // When
        repository.updateQuestionVisibility(questionId, isHidden)

        // Then
        verify(questionDao).updateQuestionVisibility(questionId, isHidden)
    }

    @Test
    fun `insertSchedule calls notificationDao insertSchedule`() = runTest {
        // Given
        val schedule = NotificationSchedule(
            id = "test-id",
            timeOfDay = "09:00"
        )

        // When
        repository.insertSchedule(schedule)

        // Then
        verify(notificationDao).insertSchedule(schedule)
    }

    @Test
    fun `updateScheduleEnabled calls notificationDao updateScheduleEnabled`() = runTest {
        // Given
        val scheduleId = "test-id"
        val isEnabled = false

        // When
        repository.updateScheduleEnabled(scheduleId, isEnabled)

        // Then
        verify(notificationDao).updateScheduleEnabled(scheduleId, isEnabled)
    }
}
