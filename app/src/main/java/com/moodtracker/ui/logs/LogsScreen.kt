package com.moodtracker.ui.logs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.datetime.*

enum class LogsViewMode {
    TIMELINE, QUESTIONS, PARTICULAR_QUESTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    repository: MoodTrackerRepository
) {
    var viewMode by remember { mutableStateOf(LogsViewMode.TIMELINE) }
    var selectedQuestionId by remember { mutableStateOf<String?>(null) }
    var answersLimit by remember { mutableIntStateOf(5) }
    
    val allAnswers by repository.getAllAnswers().collectAsState(initial = emptyList())
    val allQuestions by repository.getAllQuestions().collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (viewMode) {
            LogsViewMode.TIMELINE -> {
                TimelineView(
                    answers = allAnswers,
                    questions = allQuestions,
                    onViewModeChange = { viewMode = it }
                )
            }
            LogsViewMode.QUESTIONS -> {
                QuestionsView(
                    repository = repository,
                    questions = allQuestions,
                    answersLimit = answersLimit,
                    onViewModeChange = { viewMode = it },
                    onQuestionSelected = { questionId ->
                        selectedQuestionId = questionId
                        viewMode = LogsViewMode.PARTICULAR_QUESTION
                    },
                    onAnswersLimitChange = { answersLimit = it }
                )
            }
            LogsViewMode.PARTICULAR_QUESTION -> {
                ParticularQuestionView(
                    repository = repository,
                    questionId = selectedQuestionId,
                    questions = allQuestions,
                    onBack = { viewMode = LogsViewMode.QUESTIONS }
                )
            }
        }
    }
}

@Composable
fun TimelineView(
    answers: List<Answer>,
    questions: List<Question>,
    onViewModeChange: (LogsViewMode) -> Unit
) {
    Column {
        // Header with mode selector
        LogsHeader(
            currentMode = LogsViewMode.TIMELINE,
            onModeChange = onViewModeChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (answers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_logs),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(answers) { answer ->
                    val question = questions.find { it.id == answer.questionId }
                    if (question != null) {
                        TimelineAnswerCard(answer = answer, question = question)
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionsView(
    repository: MoodTrackerRepository,
    questions: List<Question>,
    answersLimit: Int,
    onViewModeChange: (LogsViewMode) -> Unit,
    onQuestionSelected: (String) -> Unit,
    onAnswersLimitChange: (Int) -> Unit
) {
    Column {
        // Header with mode selector
        LogsHeader(
            currentMode = LogsViewMode.QUESTIONS,
            onModeChange = onViewModeChange
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Answers limit selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.show_last_n_answers, answersLimit),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row {
                FilterChip(
                    onClick = { onAnswersLimitChange(5) },
                    label = { Text("5") },
                    selected = answersLimit == 5
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    onClick = { onAnswersLimitChange(10) },
                    label = { Text("10") },
                    selected = answersLimit == 10
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    onClick = { onAnswersLimitChange(20) },
                    label = { Text("20") },
                    selected = answersLimit == 20
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (questions.isEmpty()) {
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(questions) { question ->
                    QuestionSummaryCard(
                        repository = repository,
                        question = question,
                        answersLimit = answersLimit,
                        onClick = { onQuestionSelected(question.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ParticularQuestionView(
    repository: MoodTrackerRepository,
    questionId: String?,
    questions: List<Question>,
    onBack: () -> Unit
) {
    val question = questions.find { it.id == questionId }
    val answers by repository.getAnswersForQuestion(questionId ?: "").collectAsState(initial = emptyList())
    
    Column {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = question?.text ?: "Unknown Question",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (answers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_answers),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(answers) { answer ->
                    AnswerCard(answer = answer)
                }
            }
        }
    }
}

@Composable
fun LogsHeader(
    currentMode: LogsViewMode,
    onModeChange: (LogsViewMode) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.logs_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                onClick = { onModeChange(LogsViewMode.TIMELINE) },
                label = { Text(stringResource(R.string.logs_timeline)) },
                selected = currentMode == LogsViewMode.TIMELINE
            )
            FilterChip(
                onClick = { onModeChange(LogsViewMode.QUESTIONS) },
                label = { Text(stringResource(R.string.logs_questions)) },
                selected = currentMode == LogsViewMode.QUESTIONS
            )
        }
    }
}

@Composable
fun TimelineAnswerCard(
    answer: Answer,
    question: Question
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = answer.answerText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (!answer.additionalNotes.isNullOrBlank()) {
                Text(
                    text = "Notes: ${answer.additionalNotes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Text(
                text = formatAnswerTime(answer.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuestionSummaryCard(
    repository: MoodTrackerRepository,
    question: Question,
    answersLimit: Int,
    onClick: () -> Unit
) {
    val recentAnswers by repository.getRecentAnswersForQuestion(question.id, answersLimit)
        .collectAsState(initial = emptyList())
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            if (recentAnswers.isEmpty()) {
                Text(
                    text = stringResource(R.string.never_answered),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Last ${recentAnswers.size} answers:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                recentAnswers.take(3).forEach { answer ->
                    Text(
                        text = "• ${answer.answerText} (${formatAnswerTime(answer.timestamp)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                if (recentAnswers.size > 3) {
                    Text(
                        text = "... and ${recentAnswers.size - 3} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AnswerCard(
    answer: Answer
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = answer.answerText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (!answer.additionalNotes.isNullOrBlank()) {
                Text(
                    text = "Notes: ${answer.additionalNotes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatAnswerTime(answer.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (answer.wasSnooze) {
                    Text(
                        text = "Snoozed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
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
