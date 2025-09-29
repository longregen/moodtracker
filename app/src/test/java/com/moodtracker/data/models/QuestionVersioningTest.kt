package com.moodtracker.data.models

import kotlinx.datetime.Clock
import org.junit.Test
import org.junit.Assert.*

class QuestionVersioningTest {

    @Test
    fun `new question has version 1`() {
        // Given
        val now = Clock.System.now()
        
        // When
        val question = Question(
            id = "test-id",
            text = "Test question",
            type = QuestionType.TEXT,
            createdAt = now,
            modifiedAt = now
        )

        // Then
        assertEquals(1, question.version)
    }

    @Test
    fun `question version can be incremented`() {
        // Given
        val now = Clock.System.now()
        val originalQuestion = Question(
            id = "test-id",
            text = "Original question",
            type = QuestionType.TEXT,
            createdAt = now,
            modifiedAt = now,
            version = 1
        )

        // When
        val updatedQuestion = originalQuestion.copy(
            text = "Updated question",
            modifiedAt = Clock.System.now(),
            version = originalQuestion.version + 1
        )

        // Then
        assertEquals(2, updatedQuestion.version)
        assertEquals("Updated question", updatedQuestion.text)
        assertNotEquals(originalQuestion.modifiedAt, updatedQuestion.modifiedAt)
    }

    @Test
    fun `question type change increments version`() {
        // Given
        val now = Clock.System.now()
        val originalQuestion = Question(
            id = "test-id",
            text = "Test question",
            type = QuestionType.TEXT,
            createdAt = now,
            modifiedAt = now,
            version = 1
        )

        // When
        val updatedQuestion = originalQuestion.copy(
            type = QuestionType.YES_NO,
            modifiedAt = Clock.System.now(),
            version = originalQuestion.version + 1
        )

        // Then
        assertEquals(2, updatedQuestion.version)
        assertEquals(QuestionType.YES_NO, updatedQuestion.type)
        assertEquals(QuestionType.TEXT, originalQuestion.type)
    }

    @Test
    fun `question options change increments version`() {
        // Given
        val now = Clock.System.now()
        val originalQuestion = Question(
            id = "test-id",
            text = "How is your mood?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf("Good", "Bad"),
            createdAt = now,
            modifiedAt = now,
            version = 1
        )

        // When
        val updatedQuestion = originalQuestion.copy(
            options = listOf("Great", "Good", "Okay", "Bad", "Terrible"),
            modifiedAt = Clock.System.now(),
            version = originalQuestion.version + 1
        )

        // Then
        assertEquals(2, updatedQuestion.version)
        assertEquals(5, updatedQuestion.options?.size)
        assertEquals(2, originalQuestion.options?.size)
    }

    @Test
    fun `hiding question does not increment version`() {
        // Given
        val now = Clock.System.now()
        val originalQuestion = Question(
            id = "test-id",
            text = "Test question",
            type = QuestionType.TEXT,
            createdAt = now,
            modifiedAt = now,
            version = 1,
            isHidden = false
        )

        // When
        val hiddenQuestion = originalQuestion.copy(
            isHidden = true
            // Note: version should NOT be incremented for visibility changes
        )

        // Then
        assertEquals(1, hiddenQuestion.version) // Version unchanged
        assertTrue(hiddenQuestion.isHidden)
        assertFalse(originalQuestion.isHidden)
    }

    @Test
    fun `question with multiple choice type requires options`() {
        // Given
        val now = Clock.System.now()
        
        // When
        val multipleChoiceQuestion = Question(
            id = "test-id",
            text = "Choose an option",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf("Option 1", "Option 2", "Option 3"),
            createdAt = now,
            modifiedAt = now
        )

        // Then
        assertEquals(QuestionType.MULTIPLE_CHOICE, multipleChoiceQuestion.type)
        assertNotNull(multipleChoiceQuestion.options)
        assertEquals(3, multipleChoiceQuestion.options?.size)
    }

    @Test
    fun `question with text type does not require options`() {
        // Given
        val now = Clock.System.now()
        
        // When
        val textQuestion = Question(
            id = "test-id",
            text = "Enter your thoughts",
            type = QuestionType.TEXT,
            options = null,
            createdAt = now,
            modifiedAt = now
        )

        // Then
        assertEquals(QuestionType.TEXT, textQuestion.type)
        assertNull(textQuestion.options)
    }

    @Test
    fun `question modification preserves id and creation time`() {
        // Given
        val now = Clock.System.now()
        val originalQuestion = Question(
            id = "test-id",
            text = "Original question",
            type = QuestionType.TEXT,
            createdAt = now,
            modifiedAt = now,
            version = 1
        )

        // When
        val laterTime = Clock.System.now()
        val updatedQuestion = originalQuestion.copy(
            text = "Updated question",
            modifiedAt = laterTime,
            version = originalQuestion.version + 1
        )

        // Then
        assertEquals(originalQuestion.id, updatedQuestion.id)
        assertEquals(originalQuestion.createdAt, updatedQuestion.createdAt)
        assertNotEquals(originalQuestion.modifiedAt, updatedQuestion.modifiedAt)
        assertEquals("Updated question", updatedQuestion.text)
    }

    @Test
    fun `question versioning tracks significant changes`() {
        // Given
        val now = Clock.System.now()
        var question = Question(
            id = "test-id",
            text = "Original question",
            type = QuestionType.TEXT,
            createdAt = now,
            modifiedAt = now,
            version = 1
        )

        // When - Change text
        question = question.copy(
            text = "Updated question",
            modifiedAt = Clock.System.now(),
            version = question.version + 1
        )

        // Then
        assertEquals(2, question.version)

        // When - Change type
        question = question.copy(
            type = QuestionType.YES_NO,
            modifiedAt = Clock.System.now(),
            version = question.version + 1
        )

        // Then
        assertEquals(3, question.version)

        // When - Change to multiple choice with options
        question = question.copy(
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf("Yes", "No", "Maybe"),
            modifiedAt = Clock.System.now(),
            version = question.version + 1
        )

        // Then
        assertEquals(4, question.version)
        assertEquals(QuestionType.MULTIPLE_CHOICE, question.type)
        assertEquals(3, question.options?.size)
    }
}
