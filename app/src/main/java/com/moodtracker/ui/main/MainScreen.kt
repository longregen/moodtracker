package com.moodtracker.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.data.models.Answer
import com.moodtracker.data.models.Question
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.ui.components.*
import com.moodtracker.ui.theme.Spacing
import com.moodtracker.utils.UIUtils
import kotlinx.coroutines.launch

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
    


    StandardScreenLayout(
        title = stringResource(R.string.main_title)
    ) {

        if (questions.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.QuestionAnswer,
                title = stringResource(R.string.empty_questions),
                subtitle = "Add questions in the Config tab to get started"
            )
        } else {
            // Questions list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.cardSpacing),
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

            Spacer(modifier = Modifier.height(Spacing.medium))

            PrimaryButton(
                text = "Answer All Questions",
                onClick = onNavigateToAnswerQuestions,
                leadingIcon = Icons.Default.PlayArrow
            )
        }
    }
}

@Composable
fun QuestionCard(
    question: Question,
    latestAnswer: Answer?,
    onClick: () -> Unit = {}
) {
    CommonCard(onClick = onClick) {
        CardTitle(text = question.text)
        
        if (latestAnswer != null) {
            val timeText = UIUtils.formatAnswerTime(latestAnswer.timestamp)
            CardSubtitle(
                text = stringResource(R.string.last_answer, latestAnswer.answerText, timeText),
                modifier = Modifier.padding(bottom = Spacing.extraSmall)
            )

        } else {
            CardSubtitle(
                text = stringResource(R.string.never_answered)
            )
        }
    }
}

