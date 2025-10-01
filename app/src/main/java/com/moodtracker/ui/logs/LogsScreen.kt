package com.moodtracker.ui.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.data.models.Answer
import com.moodtracker.data.models.Question
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.ui.components.*
import com.moodtracker.ui.theme.Spacing
import com.moodtracker.utils.ExportUtils
import com.moodtracker.utils.UIUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class LogsViewMode {
    TIMELINE, QUESTIONS, PARTICULAR_QUESTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    repository: MoodTrackerRepository
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf(LogsViewMode.TIMELINE) }
    var selectedQuestionId by remember { mutableStateOf<String?>(null) }
    var answersLimit by remember { mutableIntStateOf(5) }

    // Filter states
    var showDateFilterDialog by remember { mutableStateOf(false) }
    var showQuestionFilterDialog by remember { mutableStateOf(false) }
    var dateRangeFilter by remember { mutableStateOf<Pair<Long?, Long?>>(null to null) }
    var selectedQuestionFilter by remember { mutableStateOf<String?>(null) }

    val allAnswers by repository.getAllAnswers().collectAsState(initial = emptyList())
    val allQuestions by repository.getAllQuestions().collectAsState(initial = emptyList())

    // CSV export launcher
    var pendingCsvContent by remember { mutableStateOf<String?>(null) }
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(pendingCsvContent?.toByteArray() ?: ByteArray(0))
                }
                Toast.makeText(context, "CSV exported successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        pendingCsvContent = null
    }
    
    // Apply filters
    val filteredAnswers = remember(allAnswers, dateRangeFilter, selectedQuestionFilter) {
        allAnswers.filter { answer ->
            val matchesDateFilter = dateRangeFilter.first?.let { start ->
                answer.timestamp.toEpochMilliseconds() >= start
            } ?: true && dateRangeFilter.second?.let { end ->
                answer.timestamp.toEpochMilliseconds() <= end
            } ?: true
            
            val matchesQuestionFilter = selectedQuestionFilter?.let { questionId ->
                answer.questionId == questionId
            } ?: true
            
            matchesDateFilter && matchesQuestionFilter
        }
    }
    
    val filteredQuestions = remember(allQuestions, selectedQuestionFilter) {
        if (selectedQuestionFilter != null) {
            allQuestions.filter { it.id == selectedQuestionFilter }
        } else {
            allQuestions
        }
    }
    
    StandardScreenLayout(
        title = stringResource(R.string.logs_title),
        headerActions = {
            // Date filter button
            IconButton(onClick = { showDateFilterDialog = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Filter by date",
                    tint = if (dateRangeFilter.first != null || dateRangeFilter.second != null)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Question filter button
            IconButton(onClick = { showQuestionFilterDialog = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter by question",
                    tint = if (selectedQuestionFilter != null)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Export button
            IconButton(onClick = {
                val csvContent = ExportUtils.exportToCSV(allAnswers, allQuestions)
                pendingCsvContent = csvContent
                val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                createDocumentLauncher.launch("mood_tracker_export_$timestamp.csv")
            }) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Export as CSV"
                )
            }
        }
    ) {
        when (viewMode) {
            LogsViewMode.TIMELINE -> {
                TimelineView(
                    answers = filteredAnswers,
                    questions = allQuestions,
                    onViewModeChange = { viewMode = it },
                    activeFilters = getActiveFilterCount(dateRangeFilter, selectedQuestionFilter)
                )
            }
            LogsViewMode.QUESTIONS -> {
                QuestionsView(
                    repository = repository,
                    questions = filteredQuestions,
                    answersLimit = answersLimit,
                    onViewModeChange = { viewMode = it },
                    onQuestionSelected = { questionId ->
                        selectedQuestionId = questionId
                        viewMode = LogsViewMode.PARTICULAR_QUESTION
                    },
                    onAnswersLimitChange = { answersLimit = it },
                    activeFilters = getActiveFilterCount(dateRangeFilter, selectedQuestionFilter)
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
    
    // Date Filter Dialog
    if (showDateFilterDialog) {
        DateFilterDialog(
            currentRange = dateRangeFilter,
            onDismiss = { showDateFilterDialog = false },
            onApply = { startDate, endDate ->
                dateRangeFilter = startDate to endDate
                showDateFilterDialog = false
            },
            onClear = {
                dateRangeFilter = null to null
                showDateFilterDialog = false
            }
        )
    }
    
    // Question Filter Dialog  
    if (showQuestionFilterDialog) {
        QuestionFilterDialog(
            questions = allQuestions,
            selectedQuestionId = selectedQuestionFilter,
            onDismiss = { showQuestionFilterDialog = false },
            onApply = { questionId ->
                selectedQuestionFilter = questionId
                showQuestionFilterDialog = false
            }
        )
    }
}

private fun getActiveFilterCount(
    dateRangeFilter: Pair<Long?, Long?>,
    selectedQuestionFilter: String?
): Int {
    var count = 0
    if (dateRangeFilter.first != null || dateRangeFilter.second != null) {
        count++
    }
    if (selectedQuestionFilter != null) count++
    return count
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilterDialog(
    currentRange: Pair<Long?, Long?>,
    onDismiss: () -> Unit,
    onApply: (Long?, Long?) -> Unit,
    onClear: () -> Unit
) {
    var startDateMillis by remember { mutableStateOf(currentRange.first) }
    var endDateMillis by remember { mutableStateOf(currentRange.second) }
    val datePickerState = rememberDatePickerState()
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter by Date Range") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // Start date button
                OutlinedButton(
                    onClick = { showStartPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = startDateMillis?.let { 
                            UIUtils.formatDateOnly(it)
                        } ?: "Select start date"
                    )
                }
                
                // End date button
                OutlinedButton(
                    onClick = { showEndPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = endDateMillis?.let {
                            UIUtils.formatDateOnly(it)
                        } ?: "Select end date"
                    )
                }
                
                if (startDateMillis != null || endDateMillis != null) {
                    TextButton(
                        onClick = {
                            startDateMillis = null
                            endDateMillis = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear dates")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(startDateMillis, endDateMillis) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // Start date picker
    if (showStartPicker) {
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            startDateMillis = it
                        }
                        showStartPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // End date picker
    if (showEndPicker) {
        val endDatePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let {
                            endDateMillis = it
                        }
                        showEndPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}

@Composable
fun QuestionFilterDialog(
    questions: List<Question>,
    selectedQuestionId: String?,
    onDismiss: () -> Unit,
    onApply: (String?) -> Unit
) {
    var selected by remember { mutableStateOf(selectedQuestionId) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter by Question") },
        text = {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = null }
                            .padding(vertical = Spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == null,
                            onClick = { selected = null }
                        )
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Text(
                            text = "All questions",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.small))
                }
                
                items(questions) { question ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = question.id }
                            .padding(vertical = Spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == question.id,
                            onClick = { selected = question.id }
                        )
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Text(
                            text = question.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(selected) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimelineView(
    answers: List<Answer>,
    questions: List<Question>,
    onViewModeChange: (LogsViewMode) -> Unit,
    activeFilters: Int = 0
) {
    Column {
        if (activeFilters > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.small),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "$activeFilters filter${if (activeFilters > 1) "s" else ""} active",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.small),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        if (answers.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.History,
                title = stringResource(R.string.empty_logs),
                subtitle = "Your answer history will appear here"
            )
        } else {
            // Sort answers by timestamp (newest first)
            val sortedAnswers = answers.sortedByDescending { it.timestamp }
            
            // Group answers by day
            val groupedAnswers = mutableListOf<Pair<String, List<Answer>>>()
            var currentGroup = mutableListOf<Answer>()
            var currentHeader = ""
            
            sortedAnswers.forEach { answer ->
                val header = UIUtils.getDayHeader(answer.timestamp)
                if (header != currentHeader) {
                    if (currentGroup.isNotEmpty()) {
                        groupedAnswers.add(currentHeader to currentGroup.toList())
                    }
                    currentHeader = header
                    currentGroup = mutableListOf(answer)
                } else {
                    currentGroup.add(answer)
                }
            }
            
            // Add the last group
            if (currentGroup.isNotEmpty()) {
                groupedAnswers.add(currentHeader to currentGroup.toList())
            }
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                groupedAnswers.forEach { (header, dayAnswers) ->
                    item {
                        Text(
                            text = header,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                top = if (groupedAnswers.first().first == header) 0.dp else Spacing.medium,
                                bottom = Spacing.small
                            )
                        )
                    }
                    
                    items(dayAnswers) { answer ->
                        val question = questions.find { it.id == answer.questionId }
                        if (question != null) {
                            TimelineAnswerCard(answer = answer, question = question)
                        }
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
    onAnswersLimitChange: (Int) -> Unit,
    activeFilters: Int = 0
) {
    Column {
        if (activeFilters > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.small),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "$activeFilters filter${if (activeFilters > 1) "s" else ""} active",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.small),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.small))
        
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
                Spacer(modifier = Modifier.width(Spacing.small))
                FilterChip(
                    onClick = { onAnswersLimitChange(10) },
                    label = { Text("10") },
                    selected = answersLimit == 10
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                FilterChip(
                    onClick = { onAnswersLimitChange(20) },
                    label = { Text("20") },
                    selected = answersLimit == 20
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        if (questions.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.History,
                title = stringResource(R.string.empty_questions),
                subtitle = "No questions to display"
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.cardSpacing)
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
        CommonHeader(
            title = question?.text ?: "Unknown Question",
            onBackClick = onBack
        )
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        if (answers.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.History,
                title = stringResource(R.string.empty_answers),
                subtitle = "No answers recorded for this question"
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.cardSpacing)
            ) {
                items(answers) { answer ->
                    AnswerCard(answer = answer)
                }
            }
        }
    }
}

@Composable
fun TimelineAnswerCard(
    answer: Answer,
    question: Question
) {
    CommonCard {
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = Spacing.small)
        )
        
        Text(
            text = "${UIUtils.formatTimeOnly(answer.timestamp)}: ${answer.answerText}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = Spacing.small)
        )
        
        if (!answer.additionalNotes.isNullOrBlank()) {
            CardSubtitle(
                text = "Notes: ${answer.additionalNotes}",
                modifier = Modifier.padding(bottom = Spacing.small)
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
    
    CommonCard(onClick = onClick) {
        CardTitle(
            text = question.text,
            modifier = Modifier.padding(bottom = Spacing.cardSpacing)
        )
            
        if (recentAnswers.isEmpty()) {
            CardSubtitle(
                text = stringResource(R.string.never_answered)
            )
        } else {
            CardCaption(
                text = "Last ${recentAnswers.size} answers:",
                modifier = Modifier.padding(bottom = Spacing.small)
            )
            
            recentAnswers.take(3).forEach { answer ->
                CardCaption(
                    text = "• ${answer.answerText} (${UIUtils.formatAnswerTime(answer.timestamp)})",
                    modifier = Modifier.padding(bottom = Spacing.extraSmall)
                )
            }
            
            if (recentAnswers.size > 3) {
                CardCaption(
                    text = "... and ${recentAnswers.size - 3} more"
                )
            }
        }
    }
}

@Composable
fun AnswerCard(
    answer: Answer
) {
    CommonCard {
        Text(
            text = answer.answerText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = Spacing.small)
        )
        
        if (!answer.additionalNotes.isNullOrBlank()) {
            CardSubtitle(
                text = "Notes: ${answer.additionalNotes}",
                modifier = Modifier.padding(bottom = Spacing.small)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardCaption(
                text = UIUtils.formatAnswerTime(answer.timestamp)
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

