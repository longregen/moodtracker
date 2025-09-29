package com.moodtracker.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.data.models.Answer
import com.moodtracker.data.models.Question
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.utils.DataUtils
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repository: MoodTrackerRepository,
    onNavigateToAnswerQuestions: () -> Unit = {},
    onNavigateToAnswerQuestion: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val questions by repository.getAllActiveQuestions().collectAsState(initial = emptyList())
    val latestAnswers by repository.getLatestAnswerForEachQuestion().collectAsState(initial = emptyList())
    


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(com.moodtracker.ui.theme.Spacing.screenPadding)
    ) {
        // Header
        Text(
            text = stringResource(R.string.main_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = com.moodtracker.ui.theme.Spacing.medium)
        )

        if (questions.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_questions),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Questions list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(com.moodtracker.ui.theme.Spacing.cardSpacing),
                modifier = Modifier.weight(1f)
            ) {
                items(questions) { question ->
                    QuestionCard(
                        question = question,
                        latestAnswer = latestAnswers.find { it.questionId == question.id },
                        onClick = { onNavigateToAnswerQuestion(question.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(com.moodtracker.ui.theme.Spacing.medium))

            // Answer all questions button
            Button(
                onClick = onNavigateToAnswerQuestions,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Answer All Questions")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionCard(
    question: Question,
    latestAnswer: Answer?,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = com.moodtracker.ui.theme.Spacing.cardElevation),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(com.moodtracker.ui.theme.Spacing.cardPadding)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = com.moodtracker.ui.theme.Spacing.small)
            )

            if (latestAnswer != null) {
                Text(
                    text = stringResource(R.string.last_answer, latestAnswer.answerText),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = com.moodtracker.ui.theme.Spacing.extraSmall)
                )

                val timeText = formatAnswerTime(latestAnswer.timestamp)
                Text(
                    text = stringResource(R.string.answered_at, timeText),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.never_answered),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun formatAnswerTime(timestamp: kotlinx.datetime.Instant): String {
    val now = Clock.System.now()
    val answerTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val nowTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    
    return when {
        answerTime.date == nowTime.date -> {
            stringResource(R.string.time_format_today, "${answerTime.hour}:${answerTime.minute.toString().padStart(2, '0')}")
        }
        answerTime.date == nowTime.date.minus(1, DateTimeUnit.DAY) -> {
            stringResource(R.string.time_format_yesterday, "${answerTime.hour}:${answerTime.minute.toString().padStart(2, '0')}")
        }
        else -> {
            stringResource(
                R.string.time_format_date,
                "${answerTime.monthNumber}/${answerTime.dayOfMonth}",
                "${answerTime.hour}:${answerTime.minute.toString().padStart(2, '0')}"
            )
        }
    }
}
