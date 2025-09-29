package com.moodtracker.utils

import com.moodtracker.data.models.QuestionType
import org.junit.Test
import org.junit.Assert.*

class DataUtilsTest {

    @Test
    fun `createDefaultQuestions returns exactly 4 questions`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        assertEquals(4, defaultQuestions.size)
    }

    @Test
    fun `createDefaultQuestions includes mood question with multiple choice`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        val moodQuestion = defaultQuestions.find { it.text.contains("mood", ignoreCase = true) }
        assertNotNull("Mood question should exist", moodQuestion)
        assertEquals(QuestionType.MULTIPLE_CHOICE, moodQuestion?.type)
        assertNotNull("Mood question should have options", moodQuestion?.options)
        assertTrue("Mood question should have at least 3 options", 
            moodQuestion?.options?.size ?: 0 >= 3)
    }

    @Test
    fun `createDefaultQuestions includes exercise question with yes_no type`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        val exerciseQuestion = defaultQuestions.find { it.text.contains("exercise", ignoreCase = true) }
        assertNotNull("Exercise question should exist", exerciseQuestion)
        assertEquals(QuestionType.YES_NO, exerciseQuestion?.type)
    }

    @Test
    fun `createDefaultQuestions includes computer time question with number type`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        val computerQuestion = defaultQuestions.find { 
            it.text.contains("computer", ignoreCase = true) || 
            it.text.contains("sitting", ignoreCase = true) 
        }
        assertNotNull("Computer time question should exist", computerQuestion)
        assertEquals(QuestionType.NUMBER, computerQuestion?.type)
    }

    @Test
    fun `createDefaultQuestions includes reminders question with text type`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        val remindersQuestion = defaultQuestions.find { it.text.contains("reminder", ignoreCase = true) }
        assertNotNull("Reminders question should exist", remindersQuestion)
        assertEquals(QuestionType.TEXT, remindersQuestion?.type)
    }

    @Test
    fun `createDefaultQuestions creates questions with unique IDs`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        val uniqueIds = defaultQuestions.map { it.id }.toSet()
        assertEquals("All question IDs should be unique", defaultQuestions.size, uniqueIds.size)
    }

    @Test
    fun `createDefaultQuestions creates questions with version 1`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        defaultQuestions.forEach { question ->
            assertEquals("All default questions should have version 1", 1, question.version)
        }
    }

    @Test
    fun `createDefaultQuestions creates questions that are not hidden`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        defaultQuestions.forEach { question ->
            assertFalse("Default questions should not be hidden", question.isHidden)
        }
    }

    @Test
    fun `createDefaultQuestions creates questions with valid timestamps`() {
        // When
        val defaultQuestions = DataUtils.createDefaultQuestions()

        // Then
        defaultQuestions.forEach { question ->
            assertNotNull("Question should have creation timestamp", question.createdAt)
            assertNotNull("Question should have modification timestamp", question.modifiedAt)
            assertEquals("Creation and modification time should be equal for new questions",
                question.createdAt, question.modifiedAt)
        }
    }

    @Test
    fun `createDefaultNotificationSchedules returns exactly 4 schedules`() {
        // When
        val defaultSchedules = DataUtils.createDefaultNotificationSchedules()

        // Then
        assertEquals(4, defaultSchedules.size)
    }

    @Test
    fun `createDefaultNotificationSchedules creates schedules with unique IDs`() {
        // When
        val defaultSchedules = DataUtils.createDefaultNotificationSchedules()

        // Then
        val uniqueIds = defaultSchedules.map { it.id }.toSet()
        assertEquals("All schedule IDs should be unique", defaultSchedules.size, uniqueIds.size)
    }

    @Test
    fun `createDefaultNotificationSchedules creates enabled schedules`() {
        // When
        val defaultSchedules = DataUtils.createDefaultNotificationSchedules()

        // Then
        defaultSchedules.forEach { schedule ->
            assertTrue("Default schedules should be enabled", schedule.isEnabled)
        }
    }

    @Test
    fun `createDefaultNotificationSchedules creates schedules with valid time format`() {
        // When
        val defaultSchedules = DataUtils.createDefaultNotificationSchedules()

        // Then
        defaultSchedules.forEach { schedule ->
            assertTrue("Schedule time should match HH:MM format",
                schedule.timeOfDay.matches(Regex("\\d{2}:\\d{2}")))
        }
    }

    @Test
    fun `createDefaultNotificationSchedules creates schedules in chronological order`() {
        // When
        val defaultSchedules = DataUtils.createDefaultNotificationSchedules()

        // Then
        val times = defaultSchedules.map { it.timeOfDay }
        val sortedTimes = times.sorted()
        assertEquals("Schedules should be in chronological order", sortedTimes, times)
    }

    @Test
    fun `generateId creates unique identifiers`() {
        // When
        val id1 = DataUtils.generateId()
        val id2 = DataUtils.generateId()
        val id3 = DataUtils.generateId()

        // Then
        assertNotEquals("IDs should be unique", id1, id2)
        assertNotEquals("IDs should be unique", id2, id3)
        assertNotEquals("IDs should be unique", id1, id3)
        assertTrue("ID should not be empty", id1.isNotEmpty())
        assertTrue("ID should not be empty", id2.isNotEmpty())
        assertTrue("ID should not be empty", id3.isNotEmpty())
    }

    @Test
    fun `getCurrentInstant returns valid instant`() {
        // When
        val timestamp1 = DataUtils.getCurrentInstant()
        Thread.sleep(1) // Ensure different timestamps
        val timestamp2 = DataUtils.getCurrentInstant()

        // Then
        assertNotNull("Timestamp should not be null", timestamp1)
        assertNotNull("Timestamp should not be null", timestamp2)
        assertTrue("Second timestamp should be after first", timestamp2 > timestamp1)
    }

    @Test
    fun `multiple calls to createDefaultQuestions produce consistent results`() {
        // When
        val questions1 = DataUtils.createDefaultQuestions()
        val questions2 = DataUtils.createDefaultQuestions()

        // Then
        assertEquals("Should create same number of questions", questions1.size, questions2.size)
        
        // Check that the same types of questions are created
        val types1 = questions1.map { it.type }.sorted()
        val types2 = questions2.map { it.type }.sorted()
        assertEquals("Should create same question types", types1, types2)
        
        // Check that the same question texts are created (order may vary)
        val texts1 = questions1.map { it.text }.sorted()
        val texts2 = questions2.map { it.text }.sorted()
        assertEquals("Should create same question texts", texts1, texts2)
    }
}
